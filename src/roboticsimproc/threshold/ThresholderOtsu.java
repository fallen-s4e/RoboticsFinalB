/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.threshold;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import roboticsimproc.lectures.CImage;

/**
 *
 * @author fallen
 */
public class ThresholderOtsu implements IThresholder {

    //<editor-fold defaultstate="collapsed" desc="OtsuThresholder">
    private static class OtsuThresholder {

        private int histData[];
        private int maxLevelValue;
        private int threshold;

        public OtsuThresholder() {
            histData = new int[256];
        }

        public int[] getHistData() {
            return histData;
        }

        public int getMaxLevelValue() {
            return maxLevelValue;
        }

        public int getThreshold() {
            return threshold;
        }

        public int doThreshold(byte[] srcData, byte[] monoData) {
            int ptr;

            // Clear histogram data
            // Set all values to zero
            ptr = 0;
            while (ptr < histData.length) {
                histData[ptr++] = 0;
            }

            // Calculate histogram and find the level with the max value
            // Note: the max level value isn't required by the Otsu method
            ptr = 0;
            maxLevelValue = 0;
            while (ptr < srcData.length) {
                int h = 0xFF & srcData[ptr];
                histData[h]++;
                if (histData[h] > maxLevelValue) {
                    maxLevelValue = histData[h];
                }
                ptr++;
            }

            // Total number of pixels
            int total = srcData.length;

            float sum = 0;
            for (int t = 0; t < 256; t++) {
                sum += t * histData[t];
            }

            float sumB = 0;
            int wB = 0;
            int wF = 0;

            float varMax = 0;
            threshold = 0;

            for (int t = 0; t < 256; t++) {
                wB += histData[t];					// Weight Background
                if (wB == 0) {
                    continue;
                }

                wF = total - wB;						// Weight Foreground
                if (wF == 0) {
                    break;
                }

                sumB += (float) (t * histData[t]);

                float mB = sumB / wB;				// Mean Background
                float mF = (sum - sumB) / wF;		// Mean Foreground

                // Calculate Between Class Variance
                float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

                // Check if new maximum found
                if (varBetween > varMax) {
                    varMax = varBetween;
                    threshold = t;
                }
            }

            // Apply threshold to create binary image
            if (monoData != null) {
                ptr = 0;
                while (ptr < srcData.length) {
                    monoData[ptr] = ((0xFF & srcData[ptr]) >= threshold) ? (byte) 255 : 0;
                    ptr++;
                }
            }

            return threshold;
        }
    }
    //</editor-fold>

    public boolean[][] threshold1(CImage ci) {
        OtsuThresholder thr = new OtsuThresholder();
        BufferedImage img = ci.getrImage();
        DataBufferByte db = (DataBufferByte) img.getData().getDataBuffer();

        byte[] srcData = db.getData(0);
        byte[] dstData = new byte[srcData.length];
        int threshold = thr.doThreshold(srcData, dstData);
        return new ThresholderSimple(threshold).threshold(ci);
    }

    @Override
    public boolean[][] threshold(CImage ci) {
        
        BufferedImage img = ci.getrImage();
        BufferedImage testedImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        //        Default derivative filter
//        Integer[][] kernelX = {{-1, 0, 1},
//            {-1, 0, 1}, {-1, 0, 1}};
//        Integer[][] kernelY = {{1, 1, 1},
//            {0, 0, 0},
//            {-1, -1, -1}};


//        Sobel filter
        Integer[][] kernelX = {{1, 0, -1},
            {2, 0, -2}, {1, 0, -1}};
        Integer[][] kernelY = {{1, 2, 1},
            {0, 0, 0},
            {-1, -2, -1}};
        Integer[][] noise = {
            {1, 1, 1, 1},
            {1, 0, 0, 1},
            {1, 0, 0, 1},
            {1, 1, 1, 1}};

//        Roberts filter
//        Integer[][] kernelX = {{0, 1}, {-1, 0}};
//        Integer[][] kernelY = {{1, 0}, {0, -1}};

        img = median(img, 9);
        BufferedImage diffX = convolution(img, kernelX);
        BufferedImage diffY = convolution(img, kernelY);
        BufferedImage sum = sumImages(diffX, diffY);
//        ImageOperations.drawFrame(sum);
//        ImageOperations.drawFrame(binarize(convolution(sum, noise)));
//        ImageIO.write(binarize(convolution(sum, noise)), "bmp", new File("trackPhotos/pohui.bmp"));
//        ImageOperations.drawFrame(convolution(binarize(sum),noise));
        return binarize(convolution(sum, noise));
    }

//<editor-fold defaultstate="collapsed" desc="Binarize">
    /**
     * Returns sum of two images Returns null if images has different size
     *
     * @param im1
     * @param im2
     * @return
     */
    public static boolean[][] binarize(BufferedImage image) {
        int h1 = image.getHeight();
        int w1 = image.getWidth();
        boolean[][] returnImage = new boolean[w1][h1];
        for (int x = 0; x < w1; x++) {
            for (int y = 0; y < h1; y++) {
                Color c1 = new Color(image.getRGB(x, y));
                int r = c1.getRed();
                int g = c1.getGreen();
                int b = c1.getBlue();
                int threshold = 1;
                returnImage[x][y] = (r+g+b)/3 > threshold;
            }
        }

        return returnImage;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Sum">
    /**
     * Returns sum of two images Returns null if images has different size
     *
     * @param im1
     * @param im2
     * @return
     */
    public static BufferedImage sumImages(BufferedImage im1, BufferedImage im2) {
        int h1 = im1.getHeight();
        int w1 = im1.getWidth();
        int h2 = im2.getHeight();
        int w2 = im2.getWidth();
        if (h1 != h2 || w1 != w2) {
            return null;
        }
        BufferedImage returnImage = new BufferedImage(w1, h1, BufferedImage.TYPE_4BYTE_ABGR);
        for (int x = 0; x < w1; x++) {
            for (int y = 0; y < h1; y++) {
                Color c1 = new Color(im1.getRGB(x, y));
                int r1 = c1.getRed();
                int g1 = c1.getGreen();
                int b1 = c1.getBlue();

                Color c2 = new Color(im2.getRGB(x, y));
                int r2 = c1.getRed();
                int g2 = c1.getGreen();
                int b2 = c1.getBlue();

                int r, b, g;
                r = Math.min(255, r1 + r2);
                g = Math.min(255, g1 + g2);
                b = Math.min(255, b1 + b2);
                returnImage.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        return returnImage;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Convolution">
    public static BufferedImage convolution(BufferedImage image, Integer[][] kernel) {
        int frame = 5;
        int kernelWidth = kernel.length;
        int kernelHeight = kernel[0].length;
        int kernelLenght = kernelHeight * kernelWidth;
        BufferedImage returnImage =
                new BufferedImage(image.getWidth() - frame,
                image.getHeight() - frame, BufferedImage.TYPE_4BYTE_ABGR);
        for (int x = 0; x < image.getWidth() - frame; x++) {
            for (int y = 0; y < image.getHeight() - frame; y++) {

                int[] sum = {0, 0, 0};
                for (int i = 0; i < kernelWidth; i++) {
                    for (int j = 0; j < kernelHeight; j++) {
                        int pixelPosX = x + (i - (kernelWidth / 2));
                        int pixelPosY = y + (j - (kernelHeight / 2));
                        if (pixelPosX < 0 || pixelPosY < 0
                                || pixelPosX >= image.getWidth()
                                || pixelPosY >= image.getHeight()) {
                            continue;
                        }
                        Color c = new Color(image.getRGB(pixelPosX, pixelPosY));
                        sum[0] += c.getRed() * kernel[i][j];
                        sum[1] += c.getGreen() * kernel[i][j];
                        sum[2] += c.getBlue() * kernel[i][j];
                    }
                }
                int r = Math.max(0, Math.min(255, Math.abs(sum[0] / kernelLenght)));
                int g = Math.max(0, Math.min(255, Math.abs(sum[1] / kernelLenght)));
                int b = Math.max(0, Math.min(255, Math.abs(sum[2] / kernelLenght)));
                returnImage.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        return returnImage;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Median Filter">
    public static BufferedImage median(BufferedImage image, int kernel) {
        int kernelWidth = kernel;
        int kernelHeight = kernel;
        int kernelLenght = kernelHeight * kernelWidth;
        BufferedImage returnImage =
                new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                int[][] sum = new int[3][kernelLenght];
                int s = 0;
                for (int i = 0; i < kernelWidth; i++) {
                    for (int j = 0; j < kernelHeight; j++) {
                        int pixelPosX = x + (i - (kernelWidth / 2));
                        int pixelPosY = y + (j - (kernelHeight / 2));
                        if (pixelPosX < 0 || pixelPosY < 0
                                || pixelPosX >= image.getWidth()
                                || pixelPosY >= image.getHeight()) {
                            sum[0][s] = 0;
                            sum[1][s] = 0;
                            sum[2][s] = 0;
                            continue;
                        }
                        Color c = new Color(image.getRGB(pixelPosX, pixelPosY));
                        sum[0][s] = c.getRed();
                        sum[1][s] = c.getGreen();
                        sum[2][s] = c.getBlue();
                        s += 1;
                    }
                }
                Arrays.sort(sum[0]);
                Arrays.sort(sum[1]);
                Arrays.sort(sum[2]);
                returnImage.setRGB(x, y, new Color(sum[0][kernelLenght / 2 + 1],
                        sum[1][kernelLenght / 2 + 1], sum[2][kernelLenght / 2 + 1]).getRGB());
            }
        }
        return returnImage;
    }
    //</editor-fold>
}

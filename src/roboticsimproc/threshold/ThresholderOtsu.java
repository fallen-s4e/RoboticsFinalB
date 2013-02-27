/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.threshold;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import roboticsimproc.lectures.CImage;

/**
 *
 * @author fallen
 */
public class ThresholderOtsu implements IThresholder{

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

    @Override
    public boolean[][] threshold(CImage ci) {
        OtsuThresholder thr = new OtsuThresholder();
        BufferedImage img = ci.getrImage();
        DataBufferByte db = (DataBufferByte) img.getData().getDataBuffer();

        byte[] srcData = db.getData(0);
        byte[] dstData = new byte[srcData.length];
        int threshold = thr.doThreshold(srcData, dstData);
        return new ThresholderSimple(threshold).threshold(ci);
    }
    
}
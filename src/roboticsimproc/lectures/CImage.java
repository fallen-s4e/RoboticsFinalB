/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.lectures;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

public class CImage {

    protected BufferedImage rImage;     // raster image
    protected String name;         // image name

    public CImage() {
        rImage = null;
    }
    // This constructor creates an empty image with width and height dimensions

    protected CImage(int w, int h) {
        rImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Color c = new Color(0, 0, 0);
        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {
                cor(i, j, c);
            }
        }
        name = "image" + w + "*" + h;
    }
// This constructor creates an image by copy

    protected CImage(CImage im) {
        rImage = new BufferedImage(im.getW(), im.getH(), BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < im.getW(); ++i) {
            for (int j = 0; j < im.getH(); ++j) {
                cor(i, j, im.cor(i, j));
            }
        }
        name = "copy image";
    }

    // from the work folder where is .class
    public CImage(String str) {
        name = str;
        try {
            // Read a file from the current folder
            File file = new File(name);
            if (file.isFile()) {
                rImage = ImageIO.read(file);
            } // Read a file from the .class folder
            else {
                URL url = getClass().getResource(name);
                if (url == null) {
                    url = new URL(name);
                }
                rImage = ImageIO.read(url);
            }
        } catch (IOException e) {
            System.out.println("File " + name + " not found! Program exits.");
            System.exit(1);
        }
// Checks whether reading of the file
        if (rImage == null) {
            System.out.println("File" + name + " is invalid! Program exits!");
            System.exit(1);
        }

    }
    // Returns the image height.

    public int getH() {
        return rImage.getHeight(null);
    }
// Returns the image width.

    public int getW() {
        return rImage.getWidth(null);
    }
    // Returns the color of pixel (x, y).

    public Color cor(int x, int y) {
        return new Color(rImage.getRGB(x, y));
    }

    // Affects a pixel(x, y) with the color c.
    public void cor(int x, int y, Color c) {
        if (c == null) {
            System.out.println("Invalid color! Program exits.");
            System.exit(1);
        }
        rImage.setRGB(x, y, c.getRGB());

    }

    // this method was rewritten
    public void replaceImage(BufferedImage im) {
        int w = im.getWidth(), h = im.getHeight();
        rImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < w; ++i) {
            for (int j = 0; j < h; ++j) {
                rImage.setRGB(i, j, im.getRGB(i, j));
            }
        }
    }

    public BufferedImage getrImage() {
        return rImage;
    }
}  // end of class cImage
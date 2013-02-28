/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.threshold;

import java.awt.Color;
import java.awt.image.BufferedImage;
import roboticsimproc.lectures.CImage;

/**
 *
 * @author fallen
 */
public class ThresholderSimple implements IThresholder{

    private int threshold;

    public ThresholderSimple(int threshold) {
        this.threshold = threshold;
    }
    
    @Override
    public boolean[][] threshold(BufferedImage img) {
        boolean res[][] = new boolean[img.getWidth()][img.getHeight()];
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[i].length; j++) {
                int r = new Color(img.getRGB(i, j)).getRed();
                int b = new Color(img.getRGB(i, j)).getBlue();
                int g = new Color(img.getRGB(i, j)).getGreen();
                int gray = (r+g+b) / 3;
                res[i][j] = gray > threshold;
            }
        }
        return res;
    }  
}

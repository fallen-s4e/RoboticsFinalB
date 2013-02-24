/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc;

import java.awt.Point;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import roboticsimproc.lectures.CImage;

/**
 *
 * @author fallen
 */
public abstract class IThresholder {

    public abstract boolean[][] threshold(CImage ci);

    public boolean[][] inversedThreshold(CImage ci) {
        boolean[][] t = threshold(ci);
        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t[i].length; j++) {
                t[i][j] = !t[i][j];
            }
        }
        return t;
    }

    public Vector<Point> getFirstPoints(boolean[][] thresholded, int numberOfPoints) {
        Vector<Point> v = new Vector<Point>();
        for (int i = 0; i < thresholded.length; i++) {
            for (int j = 0; j < thresholded[i].length; j++) {
                if (numberOfPoints <= 0) {
                    return v;
                }
                if (thresholded[i][j]) {
                    v.add(new Point(i, j));
                    numberOfPoints--;
                }
            }
        }
        return v;
    }

    public Vector<Point> getFirstRandomPoints(boolean[][] thresholded,
            int numberOfPoints) {
        Vector<Point> points = getFirstPoints(thresholded, thresholded.length*thresholded[0].length);
        double prob = ((double)numberOfPoints) / ((double)points.size());
        Vector<Point> res = new Vector<Point>();
        for (Point point : points) {
            if (new Random().nextInt(1000) < 1000*prob) {
                res.add(point);
            }
        }
        return res;
    }
}

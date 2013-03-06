/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Random;
import java.util.Vector;
import roboticsimproc.lectures.CImage;

/**
 *
 * @author fallen
 */
public class ImProcUtils {

    public static final double MIN_EUCLID_DISTANCE = 10;

    public static boolean[][] extendObstacles(boolean[][] arr, int thinkness) {
        boolean[][] res = new boolean[arr.length][arr[0].length];
        int t = thinkness / 2;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                res[i][j] = false;
            }
        }

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j]) {
                    for (int i1 = -t; i1 < t; i1++) {
                        for (int j1 = -t; j1 < t; j1++) {
                            try {
                                if (i1 * i1 + j1 * j1 < thinkness * thinkness) {
                                    res[i + i1][j + j1] = true;
                                }
                            } catch (IndexOutOfBoundsException ex) {
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    public static boolean[][] inversedThreshold(boolean[][] thresholded) {
        for (int i = 0; i < thresholded.length; i++) {
            for (int j = 0; j < thresholded[i].length; j++) {
                thresholded[i][j] = !thresholded[i][j];
            }
        }
        return thresholded;
    }

    public static final Vector<Point> getCornerObstacles(boolean[][] thresholded) {
        Vector<Point> v = new Vector<Point>(0);
        v.add(new Point(0, 0));
        v.add(new Point(0, thresholded[0].length - 1));
        v.add(new Point(thresholded.length - 1, 0));
        v.add(new Point(thresholded.length - 1, thresholded[0].length - 1));
        return v;
    }

    public static Vector<Point> getFirstPoints(boolean[][] thresholded, int numberOfPoints) {
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

    public static Vector<Point> getFirstRandomPoints(boolean[][] thresholded,
            int numberOfPoints) {
        Vector<Point> points = getFirstPoints(thresholded, thresholded.length * thresholded[0].length);
        double prob = ((double) numberOfPoints) / ((double) points.size());
        Vector<Point> res = new Vector<Point>();
        for (Point point : points) {
            if (new Random().nextInt(1000) < 1000 * prob) {
                res.add(point);
            }
        }
        return res;
    }

    public static double euclideanDistance(Point p1, Point p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static Point findClosestEucl(Point p, Vector<Point> ps) {
        Point bestP = ps.get(0);
        for (int i = 0; i < ps.size(); i++) {
            if (ps.get(i) != p) {
                if (euclideanDistance(bestP, p) > euclideanDistance(bestP, ps.get(i))) {
                    bestP = ps.get(i);
                }
            }
        }
        return bestP;
    }

    public static double findClosestEuclD(Point p, Vector<Point> ps) {
        return euclideanDistance(p, findClosestEucl(p, ps));
    }

    public static BufferedImage deepCopyImage(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public static boolean isObstacleBetweenPoints(Point p1, Point p2,
            boolean[][] obstacles) {
        Vector<Point> line = bresenhamLine(p1, p2);
        for (int i = 0; i < line.size(); i++) {
            if (obstacles[line.get(i).x][line.get(i).y]) {
                return true;
            }
        }
        return false;
    }

    //<editor-fold defaultstate="collapsed" desc="bresenham algo">
    public static interface OnEachPointBehaviour {
        public boolean onEachPoint(Point p);
    }
    
    public static Vector<Point> bresenhamLine(Point p1, Point p2) {
        final Vector<Point> v = new Vector<Point>();
        throughBresenham(p1, p2, new OnEachPointBehaviour() {

            @Override
            public boolean onEachPoint(Point p) {
                v.add(p);
                return false;
            }
        });
        return v;
    }
    
    /** there must be a way to optimize it */
    public static void throughBresenham(Point p1, Point p2, 
            OnEachPointBehaviour behaviour) {
            int x0 = p1.x, x1 = p2.x;
        int y0 = p1.y, y1 = p2.y;

        int deltax = (x1 - x0);
        int deltay = (y1 - y0);
        double error = 0;

        if (Math.abs(deltax) > Math.abs(deltay)) {
            double deltaerr = Math.abs(((double) deltay) / ((double) deltax));
            int yStep = y0 < y1 ? 1 : -1;
            if (x0 > x1) {
                int y = y1;
                for (int x = x1; x >= x0; x--) {
                    if (behaviour.onEachPoint(new Point(x, y))) { return; }
                    error += deltaerr;
                    if (error >= 0.5) {
                        y -= yStep;
                        error = error - 1.0;
                    }
                }
            } else {
                int y = y0;
                for (int x = x0; x <= x1; x++) {
                    if (behaviour.onEachPoint(new Point(x, y))) { return; }
                    error += deltaerr;
                    if (error >= 0.5) {
                        y += yStep;
                        error = error - 1.0;
                    }
                }
            }
        } else {
            double deltaerr = Math.abs(((double) deltax) / ((double) deltay));
            int xStep = x0 < x1 ? 1 : -1;
            if (y0 > y1) {
                int x = x1;
                for (int y = y1; y >= y0; y--) {
                    if (behaviour.onEachPoint(new Point(x, y))) { return; }
                    error += deltaerr;
                    if (error >= 0.5) {
                        x -= xStep;
                        error = error - 1.0;
                    }
                }
            } else {
                int x = x0;
                for (int y = y0; y <= y1; y++) {
                    if (behaviour.onEachPoint(new Point(x, y))) { return; }
                    error += deltaerr;
                    if (error >= 0.5) {
                        x += xStep;
                        error = error - 1.0;
                    }
                }
            }
        }
    }
    //</editor-fold>
}

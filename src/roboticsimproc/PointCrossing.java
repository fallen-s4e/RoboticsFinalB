/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.Vector;

/**
 *
 * @author fallen
 */
public class PointCrossing {

    //<editor-fold desc="non static" defaultstate="collapsed">
    private final Point point1;
    private final Point point2;
    private final Point crossing;

    public PointCrossing(Point point1, Point point2) {
        this.point1 = point1;
        this.point2 = point2;
        this.crossing = new Point((point1.x + point2.x) / 2, (point1.y + point2.y) / 2);
    }

    public Point getCrossing() {
        return crossing;
    }

    public Point getPoint1() {
        return point1;
    }

    public Point getPoint2() {
        return point2;
    }
    //</editor-fold>

    //<editor-fold desc="static" defaultstate="collapsed">
    public static double getMinDistance(Point p, Vector<Point> ps) {
        double minDist = ImProcUtils.euclideanDistance(ps.get(0), p);
        for (int i = 0; i < ps.size(); i++) {
            double t = ImProcUtils.euclideanDistance(p, ps.get(i));
            if (t < minDist) {
                minDist = t;
            }
        }
        return minDist;
    }

    /**
     * returns points for each points crossing
     */
    public static Vector<PointCrossing> pointCrossings(Vector<Point> points,
            boolean[][] thresholded) {
        Vector<PointCrossing> res = new Vector<PointCrossing>();
        boolean[][] extended = ImProcUtils.extendObstacles(
                thresholded, (int) ImProcUtils.MIN_EUCLID_DISTANCE);
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                Point p1 = points.get(i);
                Point p2 = points.get(j);
                PointCrossing pc = new PointCrossing(p1, p2);
                if (!extended[pc.crossing.x][pc.crossing.y]) {
                    res.add(pc);
                }
                /*
                 * // variant 1 if (euclideanDistance(p1, p2) >
                 * MIN_EUCLID_DISTANCE) { res.add(pc); }
                 */
            }
        }
        return res;
    }

    public static Vector<PointCrossing> unconcentrateCrossings(
            Vector<PointCrossing> crossings, int distance, int w, int h) {
        boolean[][] arr = new boolean[h][w]; // all must be false by default
        Vector<PointCrossing> res = new Vector<PointCrossing>();
        int d = distance / 2;
        for (PointCrossing crossing : crossings) {
            int x = crossing.crossing.x;
            int y = crossing.crossing.y;
            if (!arr[y][x]) { // not visited
                res.add(crossing);
                for (int i = -d; i < d; i++) {
                    for (int j = -d; j < d; j++) {
                        try {
                            if (i * i + j * j < d * d) {
                                arr[i + y][j + x] = true;
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    }
                }
            }
        }
        return res;
    }

    public static Vector<PointCrossing> filterBadCrossings(
            Vector<PointCrossing> crossings, boolean[][] thr) {
        Vector<PointCrossing> res = new Vector<PointCrossing>();
        for (PointCrossing crossing : crossings) {
            if (isBadCrossing(crossing, thr) == null) {
                res.add(crossing);
            }
        }
        return res;
    }

    /**
     * returns null if is not a bad, or a point which is an obstacle in the middle
     */
    public static Point isBadCrossing(PointCrossing pc, boolean[][] thr) {
        Vector<Point> pointsOnPath = ImProcUtils.bresenhamLine(pc.point1, pc.point2);

        int i = 0;
        Point p = pointsOnPath.get(i++);
        int c1 = 0, c2 = 0, c3 = 0;

        try {
            while (thr[p.x][p.y]) {   // skipping starting obstacles
                p = pointsOnPath.get(i++);
                c1++;
            }

            while (!thr[p.x][p.y]) {  // skipping all non-obstacles
                p = pointsOnPath.get(i++);
                c2++;
            }

            while (thr[p.x][p.y]) {   // skipping obstacles again
                p = pointsOnPath.get(i++);
                c3++;
            }
            p = pointsOnPath.get(i);
        } catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }

        if (!thr[p.x][p.y]) { // non-obstacle appeared. Bad crossing!
            System.out.println("appeated( p0 = " + p);
            System.out.println("appeated( p1 = " + pc.crossing);
            System.out.println("appeated( p2 = " + pc.point1);
            System.out.println("appeated( p3 = " + pc.point2);
            System.out.println("c1 = " + c1);
            System.out.println("c2 = " + c2);
            System.out.println("c3 = " + c3);
            return p;
        }
        return null;
    }
    //</editor-fold>
}

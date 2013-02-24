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
public class PathFinder {

    public static class PointCrossing {

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
    }

    public static double euclideanDistance(Point p1, Point p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double getMinDistance(Point p, Vector<Point> ps) {
        double minDist = euclideanDistance(ps.get(0), p);
        for (int i = 0; i < ps.size(); i++) {
            double t = euclideanDistance(p, ps.get(i));
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
        Vector<PointCrossing> res = new Vector<PathFinder.PointCrossing>();
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
}

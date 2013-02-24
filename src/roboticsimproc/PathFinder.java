/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc;

import java.awt.Point;
import java.util.Vector;

/**
 *
 * @author fallen
 */
public class PathFinder {
    private static final double MIN_EUCLID_DISTANCE = 15;

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
        double dx = p1.x-p2.x;
        double dy = p1.y-p2.y;
        return Math.sqrt(dx*dx + dy*dy);
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
    public static Vector<PointCrossing> pointCrossings(Vector<Point> points) {
        Vector<PointCrossing> res = new Vector<PathFinder.PointCrossing>();
        for (int i = 0; i < points.size(); i++) {
            for (int j = i+1; j < points.size(); j++) {
                Point p1 = points.get(i);
                Point p2 = points.get(j);
                PointCrossing pc = new PointCrossing(p1, p2);
                if (getMinDistance(pc.crossing, points) > MIN_EUCLID_DISTANCE) {
                    res.add(pc);
                }
                /*
                if (euclideanDistance(p1, p2) > MIN_EUCLID_DISTANCE) {
                    res.add(pc);
                }
                */
            }
        }
        return res;
    }
}

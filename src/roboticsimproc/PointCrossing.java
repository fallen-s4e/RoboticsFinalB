/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.*;

/**
 *
 * @author fallen
 */
public class PointCrossing extends Point {

    //<editor-fold desc="non static" defaultstate="collapsed">
    private final Point point1;
    private final Point point2;
    private Point closestObstacle = null;
    private final Vector<Point> points;

    public PointCrossing(Point point1, Point point2, Vector<Point> points) {
        super(new Point((point1.x + point2.x) / 2, (point1.y + point2.y) / 2));
        this.point1 = point1;
        this.point2 = point2;
        this.points = points;
    }

    /** lazy method */
    public Point getClosestObstacle() {
        if (closestObstacle == null) {
            closestObstacle = ImProcUtils.findClosestEucl(this, points);
        }
        return closestObstacle;
    }

    public double getClosestObstacleD() {
        return ImProcUtils.euclideanDistance(this, getClosestObstacle());
    }

    public Point getCrossing() {
        return this;
    }

    public Point getPoint1() {
        return point1;
    }

    public Point getPoint2() {
        return point2;
    }

    @Override
    public String toString() {
        return String.format("(PointCrossing: pc = %s, p1 = %s, p2 = %s)",
                super.toString(), point1.toString(), point2.toString());
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

    //<editor-fold desc="crossing finder" defaultstate="collapsed">
    /**
     * returns points for each points crossing
     */
    public static Vector<PointCrossing> pointCrossings(int numberOfPoints,
            Vector<Point> points, boolean[][] extended, int accuracy) {
        Vector<PointCrossing> res = new Vector<PointCrossing>();
        Random r = new Random();
        for (int i = 0; i < numberOfPoints; i++) {
            int x = r.nextInt(extended.length);
            int y = r.nextInt(extended[0].length);
            PointCrossing pc = generateNewPoint(new Point(x, y), points, extended, accuracy);
            if (pc != null) {
                res.add(pc);
            }
        }
        return res;
    }

    private static PointCrossing generateNewPoint(final Point point,
            Vector<Point> points, boolean[][] array, int sufficientAccuracyDelta) {
        Point aPoint = point;

        int i = 0;
        for (i = 0; i < 200; i++) {
            // finding closest obstacle point and a point opposite to it
            Point closest = ImProcUtils.findClosestEucl(aPoint, points);
            Direction oppDir = Direction.createFromPoints(aPoint, closest).rotate(Math.PI);
            Point oppClosest = ImProcUtils.findObstacleOnPath(aPoint, oppDir, array,
                    ImProcUtils.Strategy.OBSTACLE_ON_LIMIT);

            // estimating distance to it
            double closestD = ImProcUtils.euclideanDistance(aPoint, closest);
            double oppClosestD = ImProcUtils.euclideanDistance(aPoint, oppClosest);

            PointCrossing middlePoint = new PointCrossing(closest, oppClosest, points);

            double accuracy = Math.abs(ImProcUtils.euclideanDistance(aPoint, middlePoint));
            if (accuracy <= sufficientAccuracyDelta) {
                return middlePoint;
            }
            aPoint = middlePoint;
        }
        return null; // bad generation
    }
    //</editor-fold>

    //<editor-fold desc="crossing filtering" defaultstate="collapsed">
    public static Vector<PointCrossing> filterBadCrossings(
            Vector<PointCrossing> crossings, boolean[][] thr) {
        Vector<PointCrossing> res = new Vector<PointCrossing>();
        for (PointCrossing crossing : crossings) {
            if (!isBadCrossing(crossing, thr)) {
                res.add(crossing);
            }
        }
        return res;
    }

    public static boolean isBadCrossing(PointCrossing pc, boolean[][] thr) {
        Vector<Point> pointsOnPath = ImProcUtils.bresenhamLine(pc.point1, pc.point2);

        int n = pointsOnPath.size();
        double blindZone = 0.40; // the zone which is not checked. 0.5 is a fully blind
        for (int i = (int) (n * blindZone); i < n * (1 - blindZone); i++) {
            Point p = pointsOnPath.get(i);
            if (thr[p.x][p.y]) {
                return true;
            }
        }
        return false;
    }

    public static Vector<Point> justCrossigns(Vector<PointCrossing> crossings) {
        Vector<Point> v = new Vector<Point>();
        for (PointCrossing pc : crossings) {
            v.add(pc.getCrossing());
        }
        return v;
    }

    public static Vector<PointCrossing> improveCrossings(
            Vector<PointCrossing> crossings) {
        Set<PointCrossing> badPoints = new HashSet<PointCrossing>();
        for (int i = 0; i < crossings.size(); i++) {
            PointCrossing pc = crossings.get(i);
            if (badPoints.contains(pc)) {
                continue;
            }
            double minDist = pc.getClosestObstacleD();
            double radius = 0.4 * minDist; // radius to search in

            // getting neighbours in a radius
            Vector<PointCrossing> neighbours = new Vector<PointCrossing>();
            for (int j = 0; j < crossings.size(); j++) {
                PointCrossing pc1 = crossings.get(j);
                if (pc1 != pc && ImProcUtils.euclideanDistance(
                        pc.getCrossing(), pc1.getCrossing()) <= radius) {
                    neighbours.add(pc1);
                }
            }
            System.out.println("neighbours.size() = " + neighbours.size());

            // getting the best neighbours amoung them
            Vector<PointCrossing> worstNeighbours = new Vector<PointCrossing>(neighbours.size());
            worstNeighbours.setSize(neighbours.size());
            Collections.copy(worstNeighbours, neighbours);
            int numBestPoints = (int) (worstNeighbours.size() / 1.2);///(int)Math.sqrt(worstNeighbours.size());
            Collections.sort(worstNeighbours, new Comparator<PointCrossing>() {

                @Override
                public int compare(PointCrossing pc1, PointCrossing pc2) {
                    return (int) (pc2.getClosestObstacleD() - pc1.getClosestObstacleD());
                }
            });
            worstNeighbours.setSize(Math.min(worstNeighbours.size(),
                    worstNeighbours.size() - numBestPoints));
            badPoints.addAll(worstNeighbours);

            System.out.println("worstNeighbours.size() = " + worstNeighbours.size());
        }

        Vector<PointCrossing> res = new Vector<PointCrossing>();
        res.addAll(crossings);
        res.removeAll(badPoints);
        return res;
    }
    //</editor-fold>
    //</editor-fold>
}

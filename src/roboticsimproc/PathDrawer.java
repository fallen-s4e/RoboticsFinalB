/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;
import roboticsimproc.PathFinder.PointCrossing;
import roboticsimproc.lectures.CImage;
import sun.dc.pr.PathFiller;

/**
 *
 * @author fallen
 */
public class PathDrawer {

    private CImage ci;
    private IThresholder thresholder = new ThresholderSimple(90);

    public PathDrawer(CImage ci) {
        this.ci = ci;
    }

    private void drawCircle(Point p, Color color, int len) {
        int x = p.x;
        int y = p.y;
        int l = len / 2;
        for (int i = -l; i < l; i++) {
            for (int j = -l; j < l; j++) {
                try {
                    if (i * i + j * j < l * l) {
                        ci.cor(x + i, y + j, color);
                    }
                } catch (IndexOutOfBoundsException ex) {
                }
            }
        }
    }

    /**
     * entry point here
     *
     * @return
     */
    public CImage drawPath() {
        boolean[][] thr = ImProcUtils.inversedThreshold(thresholder.threshold(ci));
        Vector<Point> points = ImProcUtils.getFirstRandomPoints(thr, 1000);
        points.addAll(ImProcUtils.getCornerObstacles(thr));
        Vector<PointCrossing> crossings = PathFinder.pointCrossings(points, thr);

        // drawing just point
        // drawPoints(points, Color.blue, 2);

        // drawing points crossing
        System.out.println("crossings.size() = " + crossings.size());
        drawPointCrossings(crossings);
        return ci;
    }

    private void drawPoints(Vector<Point> points, Color color, int thickness) {
        for (int i = 0; i < points.size(); i++) {
            drawCircle(points.get(i), color, thickness);
        }
    }

    private void drawPointCrossings(Vector<PathFinder.PointCrossing> points) {
        for (int i = 0; i < points.size(); i++) {
            PointCrossing crossing = points.get(i);
            drawCircle(crossing.getPoint1(), Color.blue, 4);
            drawCircle(crossing.getPoint2(), Color.blue, 4);
            drawCircle(crossing.getCrossing(), Color.red, 4);
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc;

import roboticsimproc.threshold.IThresholder;
import roboticsimproc.threshold.ThresholderSimple;
import java.awt.Color;
import java.awt.Point;
import java.util.Vector;
import roboticsimproc.PointCrossing;
import roboticsimproc.graph.GraphMakerSparseInRadius;
import roboticsimproc.graph.IGraph;
import roboticsimproc.graph.IGraphMaker;
import roboticsimproc.lectures.CImage;
import roboticsimproc.lectures.cImageZoom;
import sun.dc.pr.PathFiller;

/**
 *
 * @author fallen
 */
public class PathDrawer {

    private cImageZoom ci;
    private IThresholder thresholder = new ThresholderSimple(90);
    private IGraphMaker grMaker = new GraphMakerSparseInRadius(30);

    public PathDrawer(cImageZoom ci) {
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
        boolean[][] extended = ImProcUtils.extendObstacles(thr, 13);
        Vector<Point> points = ImProcUtils.getFirstRandomPoints(thr, 1000);
        points.addAll(ImProcUtils.getCornerObstacles(thr));
        Vector<PointCrossing> crossings = PointCrossing.pointCrossings(points, thr);

        // drawing just point
        // drawPoints(points, Color.blue, 2);

        // drawing points crossing
        crossings = PointCrossing.unconcentrateCrossings(crossings, 11, thr.length, thr[0].length);
        
        // drawing extended
        drawExtended(extended);
        
        // crossings.setSize(50);
        System.out.println("crossings.size() = " + crossings.size());
        crossings = PointCrossing.filterBadCrossings(crossings, extended);
        System.out.println("crossings.size() = " + crossings.size());
        drawPointCrossings(crossings);
        
        // temp >>>
        // crossings = PointCrossing.filterBadCrossings(crossings, extended);

        ci.ZoomDoubleXY();//ci.ZoomDoubleXY();
        // drawPointCrossings(crossings);
        return ci;
    }

    private void drawPoints(Vector<Point> points, Color color, int thickness) {
        for (int i = 0; i < points.size(); i++) {
            drawCircle(points.get(i), color, thickness);
        }
    }

    private void drawPointCrossings(Vector<PointCrossing> points) {
        for (int i = 0; i < points.size(); i++) {
            PointCrossing crossing = points.get(i);
            drawCircle(crossing.getPoint1(), Color.blue, 4);
            drawCircle(crossing.getPoint2(), Color.blue, 4);
            drawCircle(crossing.getCrossing(), Color.red, 4);
        }
    }

    private void drawPointCrossingsV1(Vector<PointCrossing> points) {
        for (int i = 0; i < points.size(); i++) {
            PointCrossing crossing = points.get(i);
            drawCircle(crossing.getPoint1(), Color.blue, 4);
            drawCircle(crossing.getPoint2(), Color.blue, 4);
            drawCircle(crossing.getCrossing(), Color.red, 4);
            drawLine(crossing.getPoint1(), crossing.getPoint2());
        }
    }

    private void drawLine(Point point1, Point point2) {
        Vector<Point> line = ImProcUtils.bresenhamLine(point1, point2);
        for (int i = 0; i < line.size(); i++) {
            Point p = line.get(i);
            ci.cor(p.x, p.y, Color.yellow);
        }
    }

    private void drawExtended(boolean[][] extended) {
        for (int i = 0; i < extended.length; i++) {
            for (int j = 0; j < extended[i].length; j++) {
                boolean b = extended[i][j];
                if (b){
                    ci.cor(i, j, Color.black);
                }
            }
        }
    }
}

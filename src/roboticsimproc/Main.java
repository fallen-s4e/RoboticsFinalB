/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc;

import roboticsimproc.threshold.IThresholder;
import roboticsimproc.threshold.ThresholderSimple;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Point;
import java.util.Vector;
import roboticsimproc.PointCrossing;
import roboticsimproc.graph.GraphFactory;
import roboticsimproc.graph.IGraph;
import roboticsimproc.graph.pathfinder.IPathFinder;
import roboticsimproc.graph.pathfinder.PathFinderDummy;
import roboticsimproc.lectures.CImage;
import roboticsimproc.lectures.cImageZoom;
import sun.dc.pr.PathFiller;

/**
 *
 * @author fallen
 */
public class Main {

    private cImageZoom ci;
    private IThresholder thresholder = new ThresholderSimple(90);
    private GraphFactory grMaker = new GraphFactory();
    private IPathFinder<Point> pf = new PathFinderDummy<Point>(3);
    
    /**
     * entry point here
     *
     * @return
     */
    public CImage run() {
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
        
        // path drawing
        Vector<Point> nodes = PointCrossing.justCrossigns(crossings);
        IGraph<Point> gr =
                grMaker.makeSparseGraph(nodes, extended.length, 
                extended[0].length, ImProcUtils.MIN_EUCLID_DISTANCE*1.8); // almost 2 euclidian dist
        drawPath(pf.findPath(gr, nodes.get(0)));

        ci.ZoomDoubleXY();//ci.ZoomDoubleXY();
        // drawPointCrossings(crossings);
        return ci;
    }
    
    //<editor-fold defaultstate="collapsed" desc="static main and constructors">
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Main(new cImageZoom("trackPhotos/foto1.jpg")).run();
            }
        });
    }
    
    public Main(cImageZoom ci) {
        this.ci = ci;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="drawings">
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
                if (b) {
                    ci.cor(i, j, Color.black);
                }
            }
        }
    }

    private void drawPath(Vector<Point> path) {
        if (path.size() < 2) {
            return;
        }
        Point prev = path.get(0);
        for (int i = 1; i < path.size(); i++) {
            drawLine(prev, path.get(i));
            prev = path.get(i);
        }
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
    //</editor-fold>
}

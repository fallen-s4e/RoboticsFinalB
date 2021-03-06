/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc;

import roboticsimproc.threshold.IThresholder;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Vector;
import roboticsimproc.graph.GraphFactory;
import roboticsimproc.graph.IGraph;
import roboticsimproc.graph.pathfinder.IPathFinder;
import roboticsimproc.graph.pathfinder.PathFinderDijkstraPoint;
import roboticsimproc.lectures.CImage;
import roboticsimproc.lectures.cImageZoom;
import roboticsimproc.threshold.ThresholderOtsu;

/**
 *
 * @author fallen
 */
public class Main {

    private cImageZoom ci;
    private IThresholder thresholder = new ThresholderOtsu();//was simple 90
    private GraphFactory grMaker = new GraphFactory();
    private IPathFinder<Point> pf = new PathFinderDijkstraPoint();//new PathFinderDummy<Point>(30);
    
    /**
     * entry point here
     */
    public CImage run() {
        boolean[][] thr = ImProcUtils.inversedThreshold(thresholder.threshold(ci.getrImage()));
        boolean[][] extended = ImProcUtils.extendObstacles(thr, 4);
        Vector<Point> obstacles = ImProcUtils.getFirstRandomPoints(thr, 1000); // actually can use less it still remains correct
        obstacles.addAll(ImProcUtils.getCornerObstacles(thr));
        Vector<PointCrossing> crossings = PointCrossing.pointCrossings(obstacles, thr);

        // drawing just point
        // drawPoints(points, Color.blue, 2);

        // drawing points crossing
        crossings = PointCrossing.unconcentrateCrossings(crossings, 11, thr.length, thr[0].length);
        System.out.println("crossings.size() = " + crossings.size());
        // crossings = PointCrossing.improveCrossings(crossings); // should fix it before using

        // drawing extended

        // crossings.setSize(50);
        System.out.println("crossings.size() = " + crossings.size());
        crossings = PointCrossing.filterBadCrossings(crossings, extended);
        System.out.println("crossings.size() = " + crossings.size());
        // temp >>>
        // crossings = PointCrossing.filterBadCrossings(crossings, extended);
        
        // path drawing
        Vector<Point> nodes = PointCrossing.justCrossigns(crossings);
        IGraph<Point> gr =
                grMaker.makeSparseGraphBestKNeighbours(nodes, extended.length, 
                extended[0].length, 7, thr, 30); // almost 2 euclidian dist
        Point start = new Point(140, 280);
        Point closest = ImProcUtils.findClosestEucl(start, nodes);
        gr.addNode(start);
        gr.addRelation(start, closest, 0);
        
        drawPath(pf.findPath(gr, start, obstacles));
        drawCircle(start, Color.CYAN, 4);
        
        ci.ZoomDoubleXY();//ci.ZoomDoubleXY();
        // drawPointCrossings(crossings);
        return ci;
    }
    
        /**
     * entry point here
     */
    public CImage runVerbose() {
        boolean[][] thr = ImProcUtils.inversedThreshold(thresholder.threshold(
                ImProcUtils.deepCopyImage(ci.getrImage())));
        boolean[][] extended = ImProcUtils.extendObstacles(thr, 4);
        Vector<Point> obstacles = ImProcUtils.getFirstRandomPoints(thr, 1000); // actually can use less it still remains correct
        obstacles.addAll(ImProcUtils.getCornerObstacles(thr));
        Vector<PointCrossing> crossings = PointCrossing.pointCrossings(obstacles, thr);

        // drawing just point
        // drawPoints(points, Color.blue, 2);

        // drawing points crossing
        crossings = PointCrossing.unconcentrateCrossings(crossings, 11, thr.length, thr[0].length);
        System.out.println("crossings.size() = " + crossings.size());
        // crossings = PointCrossing.improveCrossings(crossings); // should fix it before using

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
                grMaker.makeSparseGraphBestKNeighbours(nodes, extended.length, 
                extended[0].length, 7, thr, 30); // almost 2 euclidian dist
        Point start = new Point(140, 280);
        Point closest = ImProcUtils.findClosestEucl(start, nodes);
        gr.addNode(start);
        gr.addRelation(start, closest, 0);
        
        drawPath(pf.findPath(gr, start, obstacles));
        drawCircle(start, Color.CYAN, 4);
        
        ci.ZoomDoubleXY();//ci.ZoomDoubleXY();
        // drawPointCrossings(crossings);
        return ci;
    }

    //<editor-fold defaultstate="collapsed" desc="static main and constructors">
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (int i = 1; i < 5; i++) {
                    new Main(new cImageZoom("trackPhotos/foto"+i+".jpg")).runVerbose();
                }
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
            drawCircle(crossing.getCrossing(), Color.yellow, 4);
        }
    }

    private void drawPointCrossingsV1(Vector<PointCrossing> points) {
        for (int i = 0; i < points.size(); i++) {
            PointCrossing crossing = points.get(i);
            drawCircle(crossing.getPoint1(), Color.blue, 4);
            drawCircle(crossing.getPoint2(), Color.blue, 4);
            drawCircle(crossing.getCrossing(), Color.yellow, 4);
            drawLine(crossing.getPoint1(), crossing.getPoint2());
        }
    }

    private void drawLine(Point point1, Point point2) {
        Vector<Point> line = ImProcUtils.bresenhamLine(point1, point2);
        for (int i = 0; i < line.size(); i++) {
            Point p = line.get(i);
            ci.cor(p.x, p.y, Color.red);
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

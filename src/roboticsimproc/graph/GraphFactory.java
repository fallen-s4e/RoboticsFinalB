/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.graph;

import java.awt.Point;
import java.util.Vector;
import roboticsimproc.ImProcUtils;
import roboticsimproc.PointCrossing;

/**
 *
 * @author fallen
 */
public class GraphFactory {

    public IGraph<Point> makeSparseGraphMaxRadius(Vector<Point> points,
            int w, int h, double maxRadius) {
        GraphSparse<Point> gr = new GraphSparse<Point>();
        for (int i = 0; i < points.size(); i++) {
            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    Point c1 = points.get(i);
                    Point c2 = points.get(j);
                    double dist = ImProcUtils.euclideanDistance(
                            c1, c2);
                    if (dist < maxRadius) {
                        gr.addNode(c1);
                        gr.addNode(c2);
                        gr.addRelation(c1, c2, dist);
                        gr.addRelation(c2, c1, dist);
                    }
                }
            }
        }
        return gr;
    }

    public IGraph<Point> makeSparseGraphBestKNeighbours(Vector<Point> points,
            int w, int h, int k) {
        return makeSparseGraphBestKNeighbours(points, w, h, k, null, 99999);
    }

    /**
     * k should not be large affects performance linearly
     */
    public IGraph<Point> makeSparseGraphBestKNeighbours(Vector<Point> points,
            int w, int h, int k, boolean[][] theshold, int maxRadius) {
        GraphSparse<Point> gr = new GraphSparse<Point>();
        for (int i = 0; i < points.size(); i++) {
            Vector<Point> bestNeighbours = new Vector<Point>();
            Point c1 = points.get(i);
            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    Point c2 = points.get(j);
                    if (bestNeighbours.size() < k) {
                        if (theshold == null || // no threshold supported
                                ((ImProcUtils.euclideanDistance(c1, c2) < maxRadius)
                                && (!ImProcUtils.isObstacleBetweenPoints(c1, c2, theshold)))) {
                            bestNeighbours.add(c2);
                        }
                    } else {
                        //trying to replace worse neighbour
                        for (int l = 0; l < bestNeighbours.size(); l++) {
                            if (ImProcUtils.euclideanDistance(c1, c2)
                                    < ImProcUtils.euclideanDistance(c1, bestNeighbours.get(l))) {
                                if (theshold == null
                                        || ((ImProcUtils.euclideanDistance(c1, c2) < maxRadius)
                                        && !ImProcUtils.isObstacleBetweenPoints(c1, c2, theshold))) {
                                    bestNeighbours.set(l, c2);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
            // number of best neighbours must be less or eq than k
            if (bestNeighbours.size() > k) { 
                throw new AssertionError("inner bug number of neighbours is more than k"); 
            }
            for (int j = 0; j < bestNeighbours.size(); j++) {
                Point c2 = bestNeighbours.get(j);
                double dist = ImProcUtils.euclideanDistance(c1, c2);
                gr.addNode(c1);
                gr.addNode(c2);
                gr.addRelation(c1, c2, dist);
                gr.addRelation(c2, c1, dist);
            }
        }
        return gr;
    }
}

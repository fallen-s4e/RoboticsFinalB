/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.graph.pathfinder;

import java.awt.Point;
import java.util.Vector;
import roboticsimproc.ImProcUtils;
import roboticsimproc.graph.IGraph;

/**
 *
 * @author fallen
 */
public class PathFinderDijkstraPoint1 implements IPathFinder<Point> {

    @Override
    public Vector<Point> findPath(IGraph<Point> gr, Point startingNode, Vector<Point> osbt) {
        int k = 10;
        Vector<Point> res = new Vector<Point>();
        Point curNode = startingNode;
        res.add(curNode);
        for (int j = 0; j < 120; j++) {
            Vector<Point> neighbours = gr.relatedWith(curNode);
            if (neighbours == null || neighbours.size() == 0) {
                return res;
            }
            int bestIdx = 0;
            for (int i = 1; i < neighbours.size(); i++) {
                if (stepPrice(gr, k, res, curNode, neighbours.get(bestIdx)) <
                        stepPrice(gr, k, res, curNode, neighbours.get(i))) {
                    bestIdx = i;
                }
            }
            curNode = neighbours.get(bestIdx);
            res.add(curNode);
        }
        return res;
    }
    
    private double stepPrice(IGraph<Point> gr, int k, Vector<Point> previousVals, 
            Point curNode, Point node) {
        double coef = 0.8;
        double v = coef*gr.relationPrice(curNode, node) +
                        (1-coef)*previousKNodesPrice(k, previousVals, curNode);
        return v;
    }
    
    private double previousKNodesPrice(int k, Vector<Point> previousVals, 
            Point curVal) {
        double res = 0;
        for (int i = 0; i < k && i < previousVals.size(); i++) {
            double v = ImProcUtils.euclideanDistance(previousVals.get(i), curVal);
            res += (v*v);
        }
        return res;
    }
}

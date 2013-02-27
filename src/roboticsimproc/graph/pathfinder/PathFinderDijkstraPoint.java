/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.graph.pathfinder;

import java.awt.Point;
import java.util.*;
import roboticsimproc.ImProcUtils;
import roboticsimproc.graph.IGraph;

/**
 *
 * @author fallen
 */
public class PathFinderDijkstraPoint implements IPathFinder<Point> {

    public Vector<Point> getPath(Point from, Point to, Map<Point, Point> bestPath) {
        Vector<Point> res = new Vector<Point>();
        while (to != from) {
            res.add(to);
            to = bestPath.get(to);
        }
        Collections.reverse(res);
        return res;
    }
    
    @Override
    public Vector<Point> findPath(final IGraph<Point> gr, Point startingNode, 
                 final Vector<Point> obst) {
        Map<Point, Double> bestPriceUnvis = new HashMap();
        Map<Point, Point> bestPath = new HashMap();
        Map<Point, Double> visited = new HashMap(); // visited
        
        Vector<Point> allNodes = gr.listNodes();
        bestPriceUnvis.put(startingNode, 0.0);
        while(visited.size() < allNodes.size()) { // while all is not visited
            // find best one
            if (!bestPriceUnvis.keySet().iterator().hasNext()) {
                break;
            }
            Point curPointV = bestPriceUnvis.keySet().iterator().next();
            for (Map.Entry<Point, Double> entry : bestPriceUnvis.entrySet()) {
                Point k = entry.getKey();
                if (bestPriceUnvis.get(k) < bestPriceUnvis.get(curPointV)) {
                    curPointV = k;
                }
            }
            final Point curPoint = curPointV;
            
            // not visited neighbours
            Vector<Point> neighbours = gr.relatedWith(curPoint);
            for (int i = 0; i < neighbours.size(); i++) {
                if (visited.containsKey(neighbours.get(i))) {
                    neighbours.remove(i);
                    --i;
                }
            }
            
            Collections.sort(neighbours, new Comparator<Point>() {

                @Override
                public int compare(Point p1, Point p2) {
                    // return (int) (gr.relationPrice(curPoint, p2) - gr.relationPrice(curPoint, p1));
                    stepPrice(gr, curPoint, p2, obst);
                    return (int) (gr.relationPrice(curPoint, p2) - 
                            gr.relationPrice(curPoint, p1));
                }
            });
            
            for (Point neigh : neighbours) {
                Double oldPrice = bestPriceUnvis.get(neigh);
                double newPrice = bestPriceUnvis.get(curPoint) + gr.relationPrice(curPoint, neigh);
                if (oldPrice==null || newPrice < oldPrice) {
                    bestPriceUnvis.put(neigh, newPrice);
                    bestPath.put(neigh, curPoint);
                }
            }
            visited.put(curPoint, bestPriceUnvis.get(curPoint));
            bestPriceUnvis.remove(curPoint);
        }
        
        // farest Node
        Point p = visited.keySet().iterator().next();
        for (Map.Entry<Point, Double> entry : visited.entrySet()) {
            Point k = entry.getKey();
            if (visited.get(k) > visited.get(p)) {
                p = k;
            }
        }
        
        return getPath(startingNode, p, bestPath);
    }

    private double stepPrice(IGraph<Point> gr,
            Point curNode, Point node, Vector<Point> osbt) {
        double coef1 = 5.5;
        double coef2 = 0.5;
        double v1 = gr.relationPrice(curNode, node);
        double v2 = ImProcUtils.findClosestEuclD(node, osbt);
        double v = coef1 * v1 + coef2 * (Math.sqrt(v2));
        return v;
    }
}

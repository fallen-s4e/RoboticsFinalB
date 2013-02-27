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
        return res;
    }
    
    @Override
    public Vector<Point> findPath(final IGraph<Point> gr, Point startingNode, Vector<Point> osbt) {
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
    
    public Vector<Point> findPath1(IGraph<Point> gr, Point startingNode, Vector<Point> osbt) {
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
                if (stepPrice(gr, k, res, curNode, neighbours.get(bestIdx), osbt)
                        < stepPrice(gr, k, res, curNode, neighbours.get(i), osbt)) {
                    bestIdx = i;
                }
            }
            curNode = neighbours.get(bestIdx);
            res.add(curNode);
        }
        return res;
    }

    private double stepPrice(IGraph<Point> gr, int k, Vector<Point> previousVals,
            Point curNode, Point node, Vector<Point> osbt) {
        double coef1 = 0.3;
        double coef2 = 0.3;
        double coef3 = 10;
        double v = coef1 * gr.relationPrice(curNode, node)
                + coef2 * previousKNodesPrice(k, previousVals, curNode)
                + coef3 * (1/ImProcUtils.findClosestEuclD(node, osbt));
        return v;
    }

    private double previousKNodesPrice(int k, Vector<Point> previousVals,
            Point curVal) {
        double res = 0;
        for (int i = 0; i < k && i < previousVals.size(); i++) {
            double v = ImProcUtils.euclideanDistance(previousVals.get(i), curVal);
            res += (v * v);
        }
        return res;
    }
}

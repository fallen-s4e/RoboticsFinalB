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

    public IGraph<Point> makeSparseGraph(Vector<Point> crossings, 
       int w, int h, double maxRadius) {
        GraphSparse<Point> gr = new GraphSparse<Point>();
        for (int i = 0; i < crossings.size(); i++) {
            for (int j = 0; j < crossings.size(); j++) {
                if (i != j) {
                    Point c1 = crossings.get(i);
                    Point c2 = crossings.get(j);
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
    
}

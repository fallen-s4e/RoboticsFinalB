/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.graph;

import java.util.Vector;
import roboticsimproc.PointCrossing;

/**
 *
 * @author fallen
 */
public interface IGraphMaker {
    public IGraph<PointCrossing> makeGraph(Vector<PointCrossing> crossings, int w, int h);
}

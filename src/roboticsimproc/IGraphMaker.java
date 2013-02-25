/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc;

import java.util.Vector;

/**
 *
 * @author fallen
 */
public interface IGraphMaker {
    public double[][] makeGraph(Vector<PointCrossing> crossings, int w, int h);
}

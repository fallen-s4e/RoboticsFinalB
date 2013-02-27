/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.graph.pathfinder;

import java.awt.Point;
import java.util.Vector;
import roboticsimproc.graph.IGraph;

/**
 *
 * @author fallen
 */
public interface IPathFinder<TNode> {
    public Vector<TNode> findPath(IGraph<TNode> gr, TNode startingNode, Vector<Point> obstacles);
}

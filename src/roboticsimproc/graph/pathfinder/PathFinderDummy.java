/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.graph.pathfinder;

import java.util.Random;
import java.util.Vector;
import roboticsimproc.graph.IGraph;

/**
 *
 * @author fallen
 */
public class PathFinderDummy<TNode> implements IPathFinder<TNode> {

    private final int numSteps;

    public PathFinderDummy(int numSteps) {
        this.numSteps = numSteps;
    }
    
    @Override
    public Vector<TNode> findPath(IGraph<TNode> gr, TNode startingNode) {
        Vector<TNode> res = new Vector<TNode>();
        res.add(startingNode);
        TNode node = startingNode;
        for (int i=0;i<numSteps;i++) {
            Vector<TNode> rels = gr.relatedWith(node);
            if (rels != null && rels.size() > 0) {
                node = rels.get(new Random().nextInt(rels.size()));
                res.add(node);
            }
        }
        return res;
    }
}

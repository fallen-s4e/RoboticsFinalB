/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author fallen
 */
public class GraphSparse<TNode> implements IGraph<TNode> {

    private Map<TNode, Map<TNode, Double>> relations =
            new HashMap<TNode, Map<TNode, Double>>();

    @Override
    public Vector<TNode> listNodes() {
        return new Vector<TNode>(relations.keySet());
    }

    @Override
    public Vector<TNode> relatedWith(TNode node) {
        return new Vector<TNode>(relations.get(node).keySet());
    }

    @Override
    public double relationPrice(TNode node1, TNode node2) {
        return relations.get(node1).get(node2);
    }

    @Override
    public void addNode(TNode node) {
        if (!relations.containsKey(node)) {
            relations.put(node, new HashMap<TNode, Double>());
        }
    }

    @Override
    public void removeNode(TNode node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addRelation(TNode from, TNode to, double price) {
        if (!relations.get(from).containsKey(to)) {
            relations.get(from).put(to, price);
        }
    }

    @Override
    public void removeRelation(TNode from, TNode to) {
        relations.get(from).remove(to);
    }
}

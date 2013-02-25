/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.graph;

import java.util.Vector;

/**
 *
 * @author fallen
 */
public interface IGraph<TNode> {
    public Vector<TNode> listNodes();
    public Vector<TNode> relatedWith(TNode node);
    public double relationPrice(TNode node1, TNode node2);
    /** add new node if it does not exist */
    public void addNode(TNode node);
    public void removeNode(TNode node);
    /** add new relation if it does not exist */
    public void addRelation(TNode from, TNode to, double price);
    public void removeRelation(TNode from, TNode to);
}

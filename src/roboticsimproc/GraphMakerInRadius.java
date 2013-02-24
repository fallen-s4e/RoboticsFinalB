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
public class GraphMakerInRadius implements IGraphMaker{
    
    private final double maxRadius;

    public GraphMakerInRadius(double maxRadius) {
        this.maxRadius = maxRadius;
    }

    @Override
    public double[][] makeGraph(Vector<PointCrossing> crossings, int w, int h) {
        double[][] res = new double[h][w];
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res[0].length; j++) {
                res[i][j] = Double.POSITIVE_INFINITY;
            }
        }
        for (int i = 0; i < crossings.size(); i++) {
            for (int j = 0; j < crossings.size(); j++) {
                if (i != j) {
                    PointCrossing c1 = crossings.get(i);
                    PointCrossing c2 = crossings.get(j);
                    double dist = ImProcUtils.euclideanDistance(
                            c1.getCrossing(), c2.getCrossing());
                    if (dist < maxRadius) {
                        res[i][j] = dist;
                        res[j][i] = dist;
                    }
                }
            }
        }
        return res;
    }
    
}

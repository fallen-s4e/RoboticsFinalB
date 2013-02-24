/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.lectures;

import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import roboticsimproc.PathDrawer;

/**
 * Corners are obstacles. 
 * 
 * Goal is a place on the image such that:
 *   maximizes number of steps(non-active pixels to reach) AND
 *   maximizes distance to the obstacles(minimum one)
 *   must be on a border?
 * 
 * Don't have to fill small breaks
 * 
 * Path:
 *   minimizing (sum(map(h, vertexes_in_path)) / 
 *               len(vertexes_in_path))
 *     where f pixel = square( min_dist_to_obstacle( pixel ) )
 *           g pixel = (sum(map(square(dist_to(pixel)), previous_vertexes_in_path)) / 
 *                      len(previous_vertexes_in_path))
 *           h pixel = 0.5*f(pixel) + 0.5*g(pixel)
 * 
 * Can process up to 1000 operations per pixel. So, I think we can assume that
 * we have only 1000 obstacle-pixels and can choose them randomly.
 * min_dist_to_obstacle can use array to estimate it fast.
 * 
 * Idea of algo:
 *   Consider all obstacle points. Find all middle points of all intersection 
 *   of the obstacle points. All obtained points is a graph. 
 *   Let's find path through the vertexes of the graph. With the minimizing 
 *   conditions described above.
 * 
 * @author fallen
 */
public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new JFrame();
                CImage ci = new CImage("trackPhotos/foto1.jpg");
                Dimension d = new Dimension(ci.getW()+20, ci.getH()+40);
                
                ci = new PathDrawer(ci).drawPath();
                PanelDraw pd = new PanelDraw(ci);
                pd.setPreferredSize(d);
                
                frame.getContentPane().add(pd);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setPreferredSize(d);
                frame.setSize(d);
                frame.setVisible(true);
            }
        });
    }
}

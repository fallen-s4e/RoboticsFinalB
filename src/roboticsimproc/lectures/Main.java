/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.lectures;

import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;

/**
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

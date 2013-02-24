/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.lectures;

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
                PanelDraw pd = new PanelDraw(ci);
                
                frame.getContentPane().add(pd);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }
}

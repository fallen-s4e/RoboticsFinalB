/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.lectures;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author fallen
 */
public class PanelDraw extends JPanel {

    private CImage im;

    public PanelDraw(CImage i) {
        super();
        im = i;
        setBackground(Color.white);
        // FIXME: wtf is it? 
        // setPreferredSize(new Dimension(im.largura(), im.altura()));
    }

    public PanelDraw(int w, int h) {
        super();
        im = null;
        setBackground(Color.white);
        setPreferredSize(new Dimension(w, h));
    }

    public void show(CImage i) {
        im = i;
        if (getWidth() != im.getW() || getHeight() != im.getH()) {
            setSize(new Dimension(im.getW(), im.getH()));
        }
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (im != null) {
            ((Graphics2D) g).drawImage(im.rImage, 0, 0, im.getW(),
                    im.getH(), null);
        }
    }
} // end of PanelDraw class
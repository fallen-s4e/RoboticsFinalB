/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.lectures;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 *
 * @author fallen
 */
public class cImageZoom extends CImage {

    private JFrame vista;             // application window
    private PanelDraw p; //  drawing window

    public void buildWindow() {
        vista = new JFrame();
        p = new PanelDraw(this);
        vista.setSize(getW(), getH());
        vista.setTitle("Imagem Application- Jorge Pais");
        vista.add(p);
        vista.setVisible(true);
        vista.repaint();
    }

    public cImageZoom(int w, int h) {
        super(w, h);
        buildWindow();
    }

    public cImageZoom(String str) {
        super(str);
        buildWindow();
    }

    public cImageZoom(CImage i) {
        super(i);
        buildWindow();

    }

    public void show() {
        p.show(this);
    }

    public void ZoomHalfXY() {
        int x, y, x1, y1;
        CImage aux = new CImage(this);
        rImage = new BufferedImage(getW() / 2, getH() / 2,
                BufferedImage.TYPE_INT_ARGB);
        for (y = 0, y1 = 0; y < aux.getH(); y += 2, ++y1) {
            for (x = 0, x1 = 0; x < aux.getW(); x += 2, ++x1) {
                cor(x1, y1, aux.cor(x, y));
            }
        }
        // FIXME : nome = "ZoomHalfXY.jpg";
    }

    public void ZoomDoubleXY() {
        int x, y, x1, y1;
        CImage aux = new CImage(this);
        Color c = new Color(0, 0, 0);
        rImage = new BufferedImage(getW() * 2, getH() * 2,
                BufferedImage.TYPE_INT_ARGB);
        for (y = 0, y1 = 0; y < aux.getH(); y++, y1 += 2) {
            for (x = 0, x1 = 0; x < aux.getW(); x++) {
                c = aux.cor(x, y);
                cor(x1, y1, c);
                cor(x1++, y1 + 1, c);
                cor(x1, y1, c);
                cor(x1++, y1 + 1, c);
            }
        }
        // FIXME: nome = "ZoomDoubleXY.jpg";
    }
} // end of cImageZoom class
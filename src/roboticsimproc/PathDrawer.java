/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;
import roboticsimproc.lectures.CImage;

/**
 *
 * @author fallen
 */
public class PathDrawer {

    private CImage ci;
    private IThresholder thresholder = new ThresholderSimple(90);

    public PathDrawer(CImage ci) {
        this.ci = ci;
    }

    private void drawCircle(Point p, Color color, int len) {
        int x = p.x;
        int y = p.y;
        int l = len / 2;
        for (int i = -l; i < l; i++) {
            for (int j = -l; j < l; j++) {
                try {
                    if (i * i + j * j < l * l) {
                        ci.cor(x + i, y + j, color);
                    }
                } catch (IndexOutOfBoundsException ex) {
                }
            }
        }
    }

    /**
     * entry point here
     *
     * @return
     */
    public CImage drawPath() {
        Vector<Point> points = thresholder.getFirstRandomPoints(
                thresholder.inversedThreshold(ci), 2000);
        printPoints(points, Color.blue, 4);
        return ci;
    }

    private void printPoints(Vector<Point> points, Color color, int thickness) {
        for (int i = 0; i < points.size(); i++) {
            drawCircle(points.get(i), color, thickness);
        }
    }
}

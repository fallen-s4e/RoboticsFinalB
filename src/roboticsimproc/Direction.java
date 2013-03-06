/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc;

import java.awt.Point;

/**
 *
 * @author fallen
 */
public class Direction {
    double dx;
    double dy;

    private Direction(double dx, double dy) {
        double factor = Math.sqrt(dx*dx + dy*dy);
        this.dx = dx/factor;
        this.dy = dy/factor;
    }
    
    public static Direction createFromPoints(Point p1, Point p2) {
        return new Direction(p2.x - p1.x, p2.y - p1.y);
    }
    
    public Point movePoint(Point p, double dist) {
        return new Point(p.x + ((int)(dx*dist)), p.y + ((int)(dy * dist)));
    }
    
    public Direction rotate(double angle) {
        int a = 10000; // accuracy
        Point p = ImProcUtils.rotatePoint(new Point((int)(a*dx), (int)(a*dy)), new Point(0, 0), angle);
        double ad = a; // the same but double to avoid implicit casts
        return new Direction(p.x/ad, p.y/ad);
    }

    @Override
    public String toString() {
        return super.toString() + "dx = " + dx + " dy = " + dy;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package roboticsimproc.threshold;

import java.awt.Point;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import roboticsimproc.lectures.CImage;

/**
 *
 * @author fallen
 */
public interface IThresholder {
    public boolean[][] threshold(CImage ci);
}

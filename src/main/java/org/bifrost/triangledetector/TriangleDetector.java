package org.bifrost.triangledetector;

public final class TriangleDetector {
    /** Normally I'd use an enum */
    public static final int SCALENE = 1;
    public static final int ISOSCELES = 2;
    public static final int EQUILATERAL = 3;
    public static final int ERROR = 4;
    
	
    /** 
     * Returns SCALENE (1), ISOSCELES (2), EQUILATERAL (3)  or ERROR (4), depending on whether
     * the arguments corresponds to such a triangle. Error if one or more arguments are less than 
     * or equal to zero.
     * 
     * @param a The first side
     * @param b The second side
     * @param c The third side
     * 
     * @return 1 (SCALENE), 2 (ISOSCELES), 3 (EQUILATERAL) or 4 (ERROR)
     * 
     */
    public static int detectTriangle(int a, int b, int c) {
	if (a <= 0 || b <= 0 || c <= 0) return ERROR;
	
	if (a == b && b == c) {
	    return EQUILATERAL;
	} else if (a == b || b == c || c == a) {
	    return ISOSCELES;
	} else {
	    return SCALENE;
	}
    }
}
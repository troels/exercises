package org.bifrost.triangledetector;

public final class TriangleDetector {
    /** Normally I'd use an enum */
    public static final int SCALENE = 1;
    public static final int ISOSCELES = 2;
    public static final int EQUILATERAL = 3;
    public static final int ERROR = 4;
    
	
    /** 
     * Returns SCALENE (1), ISOSCELES (2), EQUILATERAL (3) or ERROR
     * (4), depending on whether the arguments corresponds to such a
     * triangle. Error if one or more arguments are less than or equal
     * to zero, or if the sides can not be made into a triangle (if
     * the sum of two of the sides, are less than or equal to the sum of the third.)

     * 
     * @param a,b,c The sides of the triangle.

     * 
     * @return 1 (SCALENE), 2 (ISOSCELES), 3 (EQUILATERAL) or 4 (ERROR)
     * 
     */
    public static int detectTriangle(int a, int b, int c) {
	if (a <= 0 || b <= 0 || c <= 0 || a + b <= c || a + c <= b || b + c <= a) return ERROR;
	
	if (a == b && b == c) {
	    return EQUILATERAL;
	} else if (a == b || b == c || c == a) {
	    return ISOSCELES;
	} else {
	    return SCALENE;
	}
    }
}
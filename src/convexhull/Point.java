/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convexhull;

import java.awt.geom.Point2D;

/**
 *
 * @author Ibrahim-Abdullah
 */
public class Point extends Point2D.Double implements Comparable<Point>{
    
    //Constructor
    public Point(double x,double y){
        super(x,y);
    }

    @Override
    public int compareTo(Point other) {
        
        if(this.x == other.x){
            //Break tie with the mangnitude of their y-coordinate
            return this.y > other.y ? 1 : -1;
        }else{
            return this.x > other.x ? 1 : -1;
        }
        
    }
}

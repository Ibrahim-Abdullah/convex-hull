/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convexhull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;

/**
 *
 * @author Ibrahim-Abdullah
 * @since October 30, 2017
 */
public class ConvexHull {

    private HashSet<Point> convexSet = new HashSet<Point>();

    public ConvexHull() {    }
    
    /**
     * Sort an array of point the order of non-decreasing x-coordinate 
     * Ties are broken by the y values.
     * 
     */
    public Point[] sortPointInIncreasingXCoordinate(Point [] points) {
        Arrays.sort(points);
        return points;
    }

    /**
     *
     * @param points
     * @return 
     */
    public HashSet<Point> findConvexHull(Point [] points) {
        
        //Sort points in array in order of thier non-decreasing x-xoordinate
        
        if(points.length <= 3){
            boolean addAll = this.convexSet.addAll(Arrays.asList(points));
            return this.convexSet;
        }
        Point [] allPoints = sortPointInIncreasingXCoordinate(points);
        Point p1 = allPoints[0];
        Point pn = allPoints[allPoints.length-1];
        ArrayList<Point> allPointsA = new ArrayList<>(Arrays.asList(allPoints));
        
        //Add the two extreme point to the convexSet
        this.convexSet.add(p1);
        this.convexSet.add(pn);
        allPointsA.remove(0);
        allPointsA.remove(allPointsA.size()-1);
        
        //Find point above and below line P1 and Pn
        ArrayList<Point> pointLeftOfLineP1Pn = findPointLeftOfLine(p1,pn,allPointsA);
        ArrayList<Point> pointRightOfLineP1Pn = findPointRightOfLine(p1,pn,allPointsA);
        
        //Find upper and lower hulls of the convex hull
        findUpperHull(pointLeftOfLineP1Pn,p1,pn);
        findLowerHull(pointRightOfLineP1Pn,pn,p1);
        
        return this.convexSet;
        
    }
    /**
     * Find the set of point that lie above the line formed by p1 and pn
     * @param p1 point
     * @param pn point
     * @param points Array of point
     * @return An array of point that lie above the line  formed by p1 and pn
     */
    public ArrayList<Point> findPointLeftOfLine(Point p1, Point pn, ArrayList<Point> points) {
        ArrayList<Point> pointsLeftOfLineP1Pn = new ArrayList<>();
        
        points.forEach((a) -> {
            double determinant = (p1.x * pn.y)+(a.x * p1.y) + (pn.x * a.y) - (a.x * pn.y)-(pn.x * p1.y)-(p1.x * a.y);
            if (determinant > 0) {
                pointsLeftOfLineP1Pn.add(a);
            }
        });
        //Point [] pointLeftOfLineP1PnArray = getPointArray2(pointsLeftOfLineP1Pn);
        return pointsLeftOfLineP1Pn;
    }

    /**
     * Find the set of point that lie below the line formed by p1 and pn
     * @param p1 point
     * @param pn points
     * @param points Array of points in the plane
     * @return An array of point that lie below line p1pn.
     */
    public ArrayList<Point> findPointRightOfLine(Point p1, Point pn, ArrayList<Point> points) {
        ArrayList<Point> pointsRightOfLineP1Pn = new ArrayList<>();
        points.forEach(new Consumer<Point>() {
            @Override
            public void accept(Point a) {
                double determinant;
                determinant = (p1.x * pn.y)+(a.x * p1.y) + (pn.x * a.y) - (a.x * pn.y)-(pn.x * p1.y)-(p1.x * a.y);
                if (determinant < 0) {
                    pointsRightOfLineP1Pn.add(a);
                }
            }
        });
        //Point [] pointRightOfLineP1PnArray = getPointArray2(pointsRightOfLineP1Pn);
        return pointsRightOfLineP1Pn;
    }

    /**
     * This method find the above Hull a convex hull
     * @param pointLeftOfLine
     * @param p1 point
     * @param pn point
     */
    public void findUpperHull(ArrayList<Point> pointLeftOfLine, Point p1, Point pn) {
        if(!pointLeftOfLine.isEmpty()){
            int index = 0;
            double MaxDistance = 0.0;
            
            for( int i = 0; i < pointLeftOfLine.size(); i++){
                double pointDistanceFromLine = getDistanceFromLine(pointLeftOfLine.get(i),p1,pn);
                if(pointDistanceFromLine > MaxDistance){
                    MaxDistance = pointDistanceFromLine;
                    index = i;
                }
            }
            Point pMax = pointLeftOfLine.get(index);
            this.convexSet.add(pMax);
            pointLeftOfLine.remove(index);
            
            //Find point on the left Line Pmax and P1
            ArrayList<Point> pointOnLeftOfP1Pmax = findPointLeftOfLine(p1,pMax,pointLeftOfLine);
            
            //Find point on the right of line pn and pMax
            ArrayList<Point> pointOnLeftOfPmaxP1 = findPointLeftOfLine(pMax,pn,pointLeftOfLine);
            //Recursively find upper hull points
            findUpperHull(pointOnLeftOfP1Pmax, p1, pMax);
            findUpperHull(pointOnLeftOfPmaxP1, pMax, pn);
            
        }
        
    }

    /**
     * 
     * @param pointRightOfLine
     * @param p1 point
     * @param pn point
     */
    public void findLowerHull(ArrayList<Point> pointRightOfLine, Point p1, Point pn) {
        if(!pointRightOfLine.isEmpty()){
            int index = 0;
            double MaxDistance = 0.0;
            
            for( int i = 0; i < pointRightOfLine.size(); i++){
                double pointDistanceFromLine = getDistanceFromLine(pointRightOfLine.get(i),p1,pn);
                if(pointDistanceFromLine > MaxDistance){
                    MaxDistance = pointDistanceFromLine;
                    index = i;
                }
            }
            Point pMin = pointRightOfLine.get(index);
            this.convexSet.add(pMin);
            pointRightOfLine.remove(index);
            
            //Find point on the left Line Pmax and P1
            ArrayList<Point> pointOnRightOfPminPn = findPointRightOfLine(pMin,pn,pointRightOfLine);
            ArrayList<Point> pointOnRightOfP1Pmin = findPointRightOfLine(p1,pMin,pointRightOfLine);
            
            //Recursively find upper hull points
            findLowerHull(pointOnRightOfPminPn, pMin, pn);
            findLowerHull(pointOnRightOfP1Pmin, p1, pMin);
        }
    }
    
    /**
     * 
     * @param a Point away from the line
     * @param p1 Point at one end of the line
     * @param pn Point at one end of the line
     * @return Distance of point a from line formed by point p1 and pn
     */
    public double getDistanceFromLine(Point a,Point p1, Point pn){
        
        double distanceBtnP1andPn =  Math.sqrt(Math.pow((p1.x-pn.x),2)+Math.pow((p1.y-pn.y),2));
        double vectorDistance =  Math.abs((a.x-p1.x)*(pn.y-p1.y)-(a.y-p1.y)*(pn.x-p1.x));
        
        return vectorDistance/distanceBtnP1andPn;
    }
    
    
    /**
     * Copy the the point from an array list to a one dimensional array
     *
     * @param pointList List of point
     * @return array of point
     */
    public static Point[] getPointArray(ArrayList<Point> pointList) {
        Point[] pointsArray = new Point[pointList.size()];
        for (int i = 0; i < pointList.size(); i++) {
            pointsArray[i] = pointList.get(i);
        }

        return pointsArray;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        ArrayList<Point> dataPoint = new ArrayList<>();

        //Read logitude and latitude from a file
        //Use try with resource approach 
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("vehicle_log.csv"))))) {

            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                if (lineCount > 0) {
                    String[] lineArray = line.split(",");
                    double xCoordinate = Double.parseDouble(lineArray[2]);
                    double yCoordinate = Double.parseDouble(lineArray[3]);

                    Point point = new Point(xCoordinate, yCoordinate);
                    dataPoint.add(point);
                }
                lineCount++;
            }
            Point [] points = ConvexHull.getPointArray(dataPoint);
            ConvexHull convexHullObject = new ConvexHull();
            //System.out.println(dataPoint.size());
            System.out.println(convexHullObject.findConvexHull(points).toString());

        } catch (FileNotFoundException e) {
            System.err.println("Eror : File Not Found" + e.toString());
        } catch (Exception e) {
            System.err.println("Eror : " + e.toString());
        }
    }

}

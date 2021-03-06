package com.konfuse.strtree;

import com.konfuse.geometry.DataObject;
import com.konfuse.geometry.Line;
import com.konfuse.geometry.Point;
import com.konfuse.geometry.Rectangle;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/**
 * Minimum bounding rectangle.
 * x1, y1 is the bottom left coordinate and
 * the upper right coordinate is x2, y2
 *
 * @Author: Konfuse
 * @Date: 2019/11/25 23:33
 */
public class MBR implements Serializable {
    private double x1;
    private double y1;
    private double x2;
    private double y2;

    public MBR() {
        this.x1 = 0;
        this.y1 = 0;
        this.x2 = 0;
        this.y2 = 0;
    }

    public MBR(MBR mbr) {
        this.x1 = mbr.x1;
        this.y1 = mbr.y1;
        this.x2 = mbr.x2;
        this.y2 = mbr.y2;
    }

    public MBR(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }



    /**
     * To get the area of mbr.
     */
    public double getArea() {
        return 1.0 * (y1 - y2) * (x1 - x2);
    }

    /**
     * To get the perimeter of mbr
     */
    public double getPerimeter() {
        return 0.0F + 2 * (x2 - x1) + 2 * (y2 - y1);
    }

    /**
     * Distance between the center of mbr and the query point
     *
     * @param point the query point
     */
    public double calculateDistance(Point point) {
        double distance = 0L;

        // distance on x
        double center = (x2 + x1) / 2.0;
        double distanceDim = center - point.getX();
        distance += distanceDim * distanceDim;

        // distance on y
        center = (y1 + y2) / 2.0;
        distanceDim = center - point.getY();
        distance += distanceDim * distanceDim;

        return Math.sqrt(distance);
    }

    /**
     * The minimum distance between the border of mbr and the query point
     *
     * @param point the query point
     */
    public double calculateDistanceToBorder(Point point) {
        double distance = 0L;
        if (point.getX() < this.x1) {
            distance += (point.getX() - this.x1) * (point.getX() - this.x1);
        } else {
            if (point.getX() > this.x2) {
                distance += (point.getX() - this.x2) * (point.getX() - this.x2);
            }
        }

        if (point.getY() < this.y1) {
            distance += (point.getY() - this.y1) * (point.getY() - this.y1);
        } else {
            if (point.getY() > this.y2) {
                distance += (point.getY() - this.y2) * (point.getY() - this.y2);
            }
        }
        return Math.sqrt(distance);
    }

    /**
     * Judge whether the point is within the radius of the rectangle.
     *
     * @param point  the query point
     * @param radius the distance boundary
     * @return true if the point is within the radius of the rectangle, else false.
     */
    public boolean intersects(Point point, double radius) {
        //if mbr contains the point, then return true directly
        if (this.contains(point)) {
            return true;
        }
        // if point is out of the mbr, use the border distance to judge.
        double distance = calculateDistanceToBorder(point);
        return radius >= distance;
    }

    /**
     * judge whether a mbr is inside
     */
    public boolean contains(MBR mbr) {
        if (mbr.x1 < x1 || mbr.x2 > x2 || mbr.y1 < y1 || mbr.y2 > y2)
            return false;
        return true;
    }

    /**
     * judge whether a point is inside
     */
    public boolean contains(Point point) {
        if (point.getX() >= x1 && point.getX() <= x2 && point.getY() >= y1 && point.getY() <= y2)
            return true;
        return false;
    }

    /**
     * judge whether part of a line segment is inside
     */
    public boolean contains(Line line) {
        // if both of the line's end points is inside, return true
        Point[] points = line.getEndPoints();
        for (Point point : points) {
            if (this.contains(point)) {
                return true;
            }
        }

        // if the line segment intersects any of the diagonals of mbr, return true
        Line diagonal1 = new Line(0, x1, y1, x2, y2);
        Line diagonal2 = new Line(0, x1, y2, x2, y1);
        if (Line.intersects(line, diagonal1) || Line.intersects(line, diagonal2))
            return true;
        return false;
    }

    /**
     * judge whether part of a rectangle is inside
     */
    public boolean contains(Rectangle rectangle) {
        return MBR.intersects(this, rectangle.getMBR());
    }

    /**
     * judge whether a data object is inside
     */
    public boolean contains(DataObject dataObject) {
        // data object has two potential type
        if (dataObject instanceof Point) {
            return contains((Point) dataObject);
        } else if (dataObject instanceof Line) {
            return contains((Line) dataObject);
        } else if (dataObject instanceof Rectangle) {
            return contains((Rectangle) dataObject);
        }
        return false;
    }

    public boolean intersects(MBR mbr) {
        if (x1 > mbr.x2 || x2 < mbr.x1 || y1 > mbr.y2 || y2 < mbr.y1) {
            return false;
        }
        return true;
    }

    public double calculateApproxDistanceToBorder(Point p){
        double ans = 0.0;
        if(p.getX() < x1){
            ans = Math.max(ans, x1 - p.getX());
        } else if (p.getX() > x2) {
            ans = Math.max(ans, p.getX() - x2);
        }
        if(p.getY() < y1){
            ans = Math.max(ans, y1 - p.getY());
        } else if (p.getY() > y2) {
            ans = Math.max(ans, p.getY() - y2);
        }
        return ans;
    }

    /**
     * Union a region, calculate the smallest circumscribed rectangle of them.
     *
     * @param region variable parameter of regions
     */
    public MBR union(MBR region) {
        if (region.x1 < x1) {
            x1 = region.x1;
        }
        if (region.x2 > x2) {
            x2 = region.x2;
        }
        if (region.y1 < y1) {
            y1 = region.y1;
        }
        if (region.y2 > y2) {
            y2 = region.y2;
        }
        return new MBR(x1, y1, x2, y2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MBR mbr = (MBR) o;
        return Double.compare(mbr.x1, x1) == 0 &&
                Double.compare(mbr.y1, y1) == 0 &&
                Double.compare(mbr.x2, x2) == 0 &&
                Double.compare(mbr.y2, y2) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, y1, x2, y2);
    }

    /**
     * Inner class MBRComparatorWithTreeNode.
     * A comparator of TreeNode, compare tree nodes by specified dimensions.
     */
    public static class MBRComparatorWithTreeNode implements Comparator<RTreeNode> {
        private int dimension;
        private boolean low;

        /**
         * @param dimension if 1, then compare tree nodes by x, else if 2, compare tree nodes by y.
         * @param low       if true, then compare tree nodes by the lower bound, else
         *                  compare tree nodes by the upper bound
         */
        public MBRComparatorWithTreeNode(int dimension, boolean low) {
            this.dimension = dimension;
            this.low = low;
        }

        @Override
        public int compare(RTreeNode e1, RTreeNode e2) {
            if (dimension == 1) {
                if (low) {
                    return Double.compare(e1.mbr.x1, e2.mbr.x1);
                } else {
                    return Double.compare(e1.mbr.x2, e2.mbr.x2);
                }
            } else {
                if (low) {
                    return Double.compare(e1.mbr.y1, e2.mbr.y1);
                } else {
                    return Double.compare(e1.mbr.y2, e2.mbr.y2);
                }
            }
        }
    }

    /**
     * Inner class MBRComparatorWithLine.
     * A comparator of Line, compare lines by specified dimensions.
     */
    public static class MBRComparatorWithLine implements Comparator<Line> {
        private int dimension;
        private boolean low;

        /**
         * @param dimension if 1, then compare lines by x, else if 2, compare lines by y.
         * @param low       if true, then compare lines by the lower bound, else
         *                  compare lines by the lower bound.
         */
        public MBRComparatorWithLine(int dimension, boolean low) {
            this.dimension = dimension;
            this.low = low;
        }

        @Override
        public int compare(Line line1, Line line2) {
            if (dimension == 1) {
                if (low) {
                    return Double.compare(line1.mbr().x1, line2.mbr().x1);
                } else {
                    return Double.compare(line1.mbr().x2, line2.mbr().x2);
                }
            } else {
                if (low) {
                    return Double.compare(line1.mbr().y1, line2.mbr().y1);
                } else {
                    return Double.compare(line1.mbr().y2, line2.mbr().y2);
                }
            }
        }
    }

    /**
     * Union a list of regions, calculate the smallest circumscribed rectangle of them.
     *
     * @param regions variable parameter of regions
     */
    public static MBR union(MBR... regions) {
        double x1 = regions[0].x1;
        double y1 = regions[0].y1;
        double x2 = regions[0].x2;
        double y2 = regions[0].y2;
        // for each dimension, find the lowest and highest values
        for (MBR region : regions) {
            if (region.x1 < x1) {
                x1 = region.x1;
            }
            if (region.x2 > x2) {
                x2 = region.x2;
            }
            if (region.y1 < y1) {
                y1 = region.y1;
            }
            if (region.y2 > y2) {
                y2 = region.y2;
            }
        }
        return new MBR(x1, y1, x2, y2);
    }

    /**
     * calculate the intersection mbr of a series of regions
     */
    public static MBR intersection(MBR... regions) {
        double x1 = regions[0].x1;
        double y1 = regions[0].y1;
        double x2 = regions[0].x2;
        double y2 = regions[0].y2;
        for (MBR region : regions) {
            x1 = Math.max(region.x1, x1);
            x2 = Math.min(region.x2, x2);
            y1 = Math.max(region.y1, y1);
            y2 = Math.min(region.y2, y2);
        }
        return new MBR(x1, y1, x2, y2);
    }

    /**
     * judge whether two mbrs intersects
     */
    public static boolean intersects(MBR mbr1, MBR mbr2) {
        if (mbr2.x1 > mbr1.x2 || mbr1.x1 > mbr2.x2 || mbr2.y1 > mbr2.y2 || mbr1.y1 > mbr2.y2) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "MBR{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }
}

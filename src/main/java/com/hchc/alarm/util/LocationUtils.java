package com.hchc.alarm.util;

import com.hchc.alarm.book.Point;
import com.hchc.alarm.book.Polygon;

import java.util.List;

/**
 * Created by liuchang on 2018/4/8.
 */
public class LocationUtils {
    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：千米)
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        //s = s*1000;
        return s;
    }

    public static boolean inside(Polygon polygon, Point point){
        boolean c = false;
        List<Point> pointList = polygon.getPointList();
        pointList.add(pointList.get(0));
        for (int i = 1; i <pointList.size(); i++) {
            if(online(pointList.get(i-1),pointList.get(i),point)){
                c = !c;
            }
        }
        return c;
    }

    private static  boolean online(Point l1,Point l2,Point point){
        return (l1.getLat()>point.getLat() != l2.getLat()>point.getLat()) && (point.getLng() >
                (l2.getLng()-l1.getLng())*(point.getLat()-l1.getLat())/(l2.getLat()-l1.getLat())+l1.getLng());
    }

}

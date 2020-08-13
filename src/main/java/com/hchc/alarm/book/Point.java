package com.hchc.alarm.book;

/**
 * @author wangrong
 * @date 2020-07-15
 */
public class Point {

    private double lat;
    private double lng;

    public Point() {
    }

    public Point(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}

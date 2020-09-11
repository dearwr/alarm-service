package com.hchc.alarm;

import com.hchc.alarm.book.Point;
import com.hchc.alarm.book.PolygonArea;
import com.hchc.alarm.util.LocationUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangrong
 * @date 2020-08-07
 */
public class Test {

    //    public static void main(String[] args) {
//        Double lat = 31.571347;
//        Double lng = 120.291652;
//        String json = "{\n" +
//                "\t\t\"polygon\": {\n" +
//                "\t\t\t\"pointList\": [{\n" +
//                "\t\t\t\t\"lat\": 31.58733011514269,\n" +
//                "\t\t\t\t\"lng\": 120.29704749584198\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.585867818618098,\n" +
//                "\t\t\t\t\"lng\": 120.28760612010956\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.581627028931052,\n" +
//                "\t\t\t\t\"lng\": 120.28142631053925\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.577239802047412,\n" +
//                "\t\t\t\t\"lng\": 120.27816474437714\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.571243591360506,\n" +
//                "\t\t\t\t\"lng\": 120.2761048078537\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.564515676410203,\n" +
//                "\t\t\t\t\"lng\": 120.27713477611542\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.561663479011184,\n" +
//                "\t\t\t\t\"lng\": 120.27910888195038\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.558811194391865,\n" +
//                "\t\t\t\t\"lng\": 120.28091132640839\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.556617069951724,\n" +
//                "\t\t\t\t\"lng\": 120.28434455394745\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.554422893906523,\n" +
//                "\t\t\t\t\"lng\": 120.28726279735565\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.55361834976046,\n" +
//                "\t\t\t\t\"lng\": 120.29129683971405\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.552813798676695,\n" +
//                "\t\t\t\t\"lng\": 120.29464423656464\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.552521232926157,\n" +
//                "\t\t\t\t\"lng\": 120.30202567577362\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.555885683670468,\n" +
//                "\t\t\t\t\"lng\": 120.31009376049042\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.559103740418053,\n" +
//                "\t\t\t\t\"lng\": 120.31369864940643\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.56583204579101,\n" +
//                "\t\t\t\t\"lng\": 120.3174751996994\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.571097337501488,\n" +
//                "\t\t\t\t\"lng\": 120.31764686107635\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.573729871862994,\n" +
//                "\t\t\t\t\"lng\": 120.31764686107635\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.577678534028337,\n" +
//                "\t\t\t\t\"lng\": 120.31593024730682\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.582650685489774,\n" +
//                "\t\t\t\t\"lng\": 120.31146705150604\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.58352809644826,\n" +
//                "\t\t\t\t\"lng\": 120.30923545360565\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.585136661750454,\n" +
//                "\t\t\t\t\"lng\": 120.30597388744354\n" +
//                "\t\t\t}, {\n" +
//                "\t\t\t\t\"lat\": 31.58689142859482,\n" +
//                "\t\t\t\t\"lng\": 120.30013740062714\n" +
//                "\t\t\t}]\n" +
//                "\t\t},\n" +
//                "\t\t\"deliveryFee\": \"3.5\",\n" +
//                "\t\t\"area\": \"11891.61\"\n" +
//                "\t}";
//        PolygonArea area = JSON.parseObject(json, PolygonArea.class);
//        Double fee = fetchPolygonFee(Collections.singletonList(area), lat, lng);
//        System.out.println(fee);
//    }
    public static void main(String[] args) {
        writeData();
    }

    private static void writeData() {
        File file = new File("E:/XSJL.DB");
        byte[] buffer = new byte[1024];
        StringBuilder sb = new StringBuilder();
        try (FileInputStream ins = new FileInputStream(file)) {
            while (ins.read(buffer) != -1) {
                sb.append(new String(buffer, StandardCharsets.UTF_8));
            }
            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Double fetchPolygonFee(List<PolygonArea> polygonAreas, Double lat, Double lng) {
        Point point = new Point(lat, lng);
        List<Double> feeList = new ArrayList<>();
        for (PolygonArea polygonArea : polygonAreas) {
            if (!LocationUtils.inside(polygonArea.getPolygon(), point)) {
                continue;
            }
            feeList.add(polygonArea.getDeliveryFee());
        }
        if (feeList.isEmpty()) {
            return null;
        }
        return feeList.stream().min(Double::compareTo).get();
    }


}

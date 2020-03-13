package com.konfuse;

import com.konfuse.fmm.FmmMatcher;
import com.konfuse.hmm.OfflineMatcher;
import com.konfuse.road.*;
import com.konfuse.tools.GenerateTestGPSPoint;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther todd
 * @Date 2020/1/8
 */
public class TestFmm {
    public static void main(String[] args) throws Exception{
        RoadMap map = RoadMap.Load(new RoadReader());
        map.construct();

//        GenerateTestGPSPoint test = new GenerateTestGPSPoint();
//        List<GPSPoint> testRoads = test.generateTestGPSPoint(map);
//        List<GPSPoint> testGPSPoint = test.generateTestCase(testRoads);
//        List<GPSPoint> testGPSPoint = readGPSPoint("199_2014-02-28.txt");

        FmmMatcher fmmMatcher = new FmmMatcher(2);
        fmmMatcher.constructUBODT(map, 3000);

        long search_time = testMatch("C:/Users/Konfuse/Desktop/1", fmmMatcher, map);
        System.out.println("Search time :" + search_time + "ms");

//        List<Road> c_path = fmmMatcher.constructCompletePathOptimized(matchedRoadPoints, map);
//        List<GPSPoint> c_path_gps = fmmMatcher.getCompletePathGPS(c_path);

//        System.out.println("************road***********");
//        test.writeAsTxt(testRoads, "output/road.txt");
//        System.out.println("***************************");

//        System.out.println("************trajectory***********");
//        test.writeAsTxt(testGPSPoint, "output/trajectory.txt");
//        System.out.println("***************************");
//
//        System.out.println("************matched***********");
//        write(matchedRoadPoints, "output/matched.txt");
//        System.out.println("***************************");
//
//        System.out.println("*******complete path*******");
//        test.writeAsTxt(c_path_gps, "output/c_path.txt");
//        System.out.println("***************************");
    }

    public static List<GPSPoint> readGPSPoint(String path) {
        List<GPSPoint> gpsPoints = new ArrayList<>();

        BufferedReader reader;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] items = line.split(";");
                double x = Double.parseDouble(items[0]);
                double y = Double.parseDouble(items[1]);
                long time = simpleDateFormat.parse(items[2]).getTime() / 1000;
                gpsPoints.add(new GPSPoint(time, x, y));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return gpsPoints;
    }

    public static void write(List<RoadPoint> points, String path) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            for (RoadPoint point : points) {
                double x = point.point().getX();
                double y = point.point().getY();
                System.out.println(x + ";" + y);
                writer.write(x + ";" + y);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static long testMatch(String path, FmmMatcher fmmMatcher, RoadMap map) {
        List<GPSPoint> gpsPoints = new ArrayList<>();
        File[] fileList = new File(path).listFiles();
        BufferedReader reader = null;
        long search_time = 0;
        int count = 0;
        int except = 0;

        for (File file : fileList) {
            try {
                reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] items = line.split(";");
                    double x = Double.parseDouble(items[0]);
                    double y = Double.parseDouble(items[1]);
                    long time = Long.parseLong(items[2]);
                    gpsPoints.add(new GPSPoint(time, x, y));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("the " + (count++) + "th trajectory is being processed: " + file.getName());
            try {
                long start = System.currentTimeMillis();
                fmmMatcher.match(gpsPoints, map, 20);
                long end = System.currentTimeMillis();
                search_time += end - start;
            } catch (Exception e) {
                e.printStackTrace();
                ++except;

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }

                try{
                    if(file.delete()) {
                        System.out.println(file.getName() + " 文件已被删除！");
                    } else {
                        System.out.println("文件删除失败！");
                    }
                } catch(Exception e3){
                    e3.printStackTrace();
                }
            }
            gpsPoints.clear();
        }
        System.out.println(except + " trajectories failed");
        return search_time;
    }
}

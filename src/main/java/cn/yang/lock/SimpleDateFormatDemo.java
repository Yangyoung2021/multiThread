package cn.yang.lock;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class SimpleDateFormatDemo {

    private final String Test;

    {
        Test = "这是代码块中的赋值";
    }

//    public SimpleDateFormatDemo(String test) {
//        Test = test;
//    }

    public static void main(String[] args) {
//        sdf();
        //平时用的给Integer对象赋值会使用valueOf()方法进行自动装箱，这是就会采用享元模式，利用缓存进行存储对象
//        Integer a = 100;
//        Integer b = 100;
//
//        System.out.println(a == b);//true
        SimpleDateFormatDemo demo = new SimpleDateFormatDemo();
        System.out.println("demo.Test = " + demo.Test);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                TemporalAccessor parseResult = formatter.parse("1998-12-11");
                System.out.println(parseResult);
            }).start();
        }
    }

    private static void sdf() {
        String format = "   yyyy-MM-dd   ".trim();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                Date parse;
                try {
                    parse = sdf.parse("1998-12-11");
                    System.out.println("parse = " + parse);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}

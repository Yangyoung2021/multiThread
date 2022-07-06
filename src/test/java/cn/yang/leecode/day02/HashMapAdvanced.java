package cn.yang.leecode.day02;

import java.util.HashMap;
import java.util.Map;

public class HashMapAdvanced {


    public static void main(String[] args) {
        HashMap<Integer, Integer> map = new HashMap<>();

        map.put(1, 1);
        map.put(16, 1);
        map.put(35, 1);

        for (Integer key : map.keySet()) {
            System.out.println("key = " + key + "\t value = " + map.get(key));
        }

        System.out.println();

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println("key = " + entry.getKey() + "\t value = " + entry.getValue());
        }

        System.out.println();

//        ArrayList<Object> list = new ArrayList<>();

        map.forEach((key, value) -> System.out.println("key = " + key + "\t value = " + value));

    }
}

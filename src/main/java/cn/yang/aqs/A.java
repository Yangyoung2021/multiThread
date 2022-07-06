package cn.yang.aqs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class A {
	public static void main(String[] args) {
//		System.out.println("Hello World!!!");
		ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<>(5);

		arrayBlockingQueue.offer("Java");
		arrayBlockingQueue.offer("Python");
		arrayBlockingQueue.offer("C++");
		arrayBlockingQueue.offer("C");
		arrayBlockingQueue.offer("Go");
		arrayBlockingQueue.offer("PHP");
		for (String s : arrayBlockingQueue) {
//			System.out.println(s);
		}

		ArrayList<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
//		String collect = String.join("\n", list.subList(0, 1));
//		System.out.print("collect = " + collect);

		HashMap<Integer, String> map = new HashMap<>();

		map.put(1, "one");
		map.put(16, "sixteen");
		map.put(35, "thirty five");


	}

}

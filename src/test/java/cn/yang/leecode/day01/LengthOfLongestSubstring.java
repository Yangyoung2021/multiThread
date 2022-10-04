package cn.yang.leecode.day01;

import java.util.HashMap;

public class LengthOfLongestSubstring {

    public static void main(String[] args) {
        String s = "au";
        System.out.println(lengthOfLongestSubstring1(s));
    }

    public static int lengthOfLongestSubstring(String s) {
        HashMap<Character, Integer> hashMap = new HashMap<>();
        String substring = s.substring(0, 0);
        System.out.println(substring);
        for (int i = 0; i < s.length(); i++) {
            if (hashMap.containsKey(s.charAt(i))) {
                int index = hashMap.get(s.charAt(i));
                hashMap.clear();
                i = index + 1;
            } else {
                hashMap.put(s.charAt(i), i);
            }
        }
        return hashMap.size();
    }

    public static int lengthOfLongestSubstring1(String s) {
        int first = 0;
        int second = 0;
        int max = 0;
        String substring = "";
        for (int i = 0; i < s.length(); i++) {
            substring = s.substring(first, second);
            if (substring.contains(String.valueOf(s.charAt(i)))) {
                max = Math.max(max, substring.length());
                first += substring.indexOf(s.charAt(i)) + 1;
            }
            second++;
            substring = s.substring(first, second);
        }
        return Math.max(max, substring.length());
    }
}

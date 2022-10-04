package cn.yang.leecode.day01;

import java.util.HashMap;

public class AddTwoNumbers {

    public static void main(String[] args) {
        ListNode l1 = new ListNode(2, new ListNode(4, new ListNode(3)));
        ListNode l2 = new ListNode(5, new ListNode(6, new ListNode(4)));

        String s = "abcdefg";
        char c = s.charAt(2);
        ListNode listNode = addTwoNumbers(l1, l2);
        while (listNode != null) {
            System.out.println(listNode.val);
            listNode = listNode.next;
        }
    }



    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode list = new ListNode();
        ListNode rs = list;
        long r1 = 0;
        long r2 = 0;
        long r1_value = 0;
        long r2_value = 0;
        while (l1 != null) {
            r1 += Math.pow(10, r1_value) * l1.val;
            r1_value++;
            l1 = l1.next;
        }
        while (l2 != null) {
            r2 += Math.pow(10, r2_value) * l2.val;
            r2_value++;
            l2 = l2.next;
        }
        long result = r1 + r2;
        long nextValue;
        while (result != 0) {
            nextValue = result / 10;
            list.val = (int) (result % 10);
            if (result > 9) {
                list.next = new ListNode();
                list = list.next;
            }
            result = nextValue;
        }
        return rs;
    }

    static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }
}

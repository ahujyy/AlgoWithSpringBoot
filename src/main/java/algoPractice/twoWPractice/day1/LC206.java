package algoPractice.twoWPractice.day1;

/**
 * 反转链表。
 * https://leetcode.com/problems/reverse-linked-list/description/
 */
public class LC206 {

    static class ListNode {
         int val;
         ListNode next;
         ListNode() {}
         ListNode(int val) { this.val = val; }
         ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode reverseList(ListNode head) {

        if(head == null)return null;
        ListNode current = head;
        ListNode next_next = null;
        ListNode next = current.next;


        while(next != null){
        
            next_next = next.next;
            next.next = current;

            //move forward
            current = next;
            next = next_next;
        }

        head.next =null;

        return current;
    }


    public static void main(String[] args){
        System.out.println("ss");
    }
}
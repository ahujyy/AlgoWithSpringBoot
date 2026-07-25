package algoPractice.twoWPractice.day1;

/**
 * https://leetcode.com/problems/merge-two-sorted-lists/description/
 * LC21
 */
public class LC21 {

     public class ListNode {
        int val;
        ListNode next;
        ListNode() {}
         ListNode(int val) { this.val = val; }
         ListNode(int val, ListNode next) { this.val = val; this.next = next; }
     }

    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode head1 = list1;
        ListNode head2 = list2;
        ListNode head=null, current = null;
        while(head1 != null && head2 != null){
            ListNode picked = null;
            if(head1.val <= head2.val ){
                picked = head1;
                head1 = head1.next;
            }else{
                picked = head2;
                head2 = head2.next;
            }
            if(head == null) head = picked;
            if(current != null) current.next = picked;
            current = picked;
        }

        if(head1 != null){
            if(head == null) head = head1;
            else if(current != null) current.next = head1;
        }

        if(head2 != null){
            if(head == null) head = head2;
            else if(current != null) current.next = head2;
        }
        
        return head;
    }
}

package algoPractice.twoWPractice.day1;

/**
 * fast & slow pointers
 * LC141
 *  https://leetcode.com/problems/linked-list-cycle/description/
 * 只要快慢针进入循环链，设快和慢节点相差 N, 每一次快比慢多走一步，距离就近一步，距离就会通过 N-1 减少到 0.就追上了。
 */

public class LC141 {

    class ListNode {
        int val;
        ListNode next;
        ListNode(int x) {
            val = x;
            next = null;
        }
    }


    public boolean hasCycle(ListNode head) {
        if(head == null) return false;

        ListNode fast = head;
        ListNode slow = head;
        while(fast.next != null && fast.next.next != null){
            fast = fast.next.next;
            slow = slow.next;
            if(fast == slow)return true;
        }
        return false;

    }
}

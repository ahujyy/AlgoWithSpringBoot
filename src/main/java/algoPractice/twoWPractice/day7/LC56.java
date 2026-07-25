package algoPractice.twoWPractice.day7;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * https://leetcode.com/problems/merge-intervals/description/ 
 * merge 区间。 多区间用二维数组构成， 如果 动态去构造（online merging），会陷入以下场景：
 * 这相当于你在手动实现一个复杂的“区间树”或“动态区间链表”，这会带来毁灭性的分情况讨论（边界地狱）：
 * 1. 当新区间进来时，它可能完全在一个已有区间内部；
 * 2.它可能跨越了两个已有区间，把它们连起来；
 * 3.它可能把前面好几个区间都吞并了（你的链表版本中用了 while(node != null && end > node.right) 就是在处理这种吞并多个节点的情况）；
 * 4.它还可能插在两个区间中间，谁也不挨着。
 * 
 * 
 * 但是如果把区间按左边元素排序（排序一定要想起 comparator，脑子不能默认只有排序简单的数才容易）。
 * merge 就好办了， 每次只用考虑两个区间， 后面区间如果起点还在前面终点的后面，就不用 merge。 merge 因为增序，只用考虑什么是终点就可以了。
 * 
 * LC56
 */
public class LC56 {

    public int[][] merge_easy(int[][] intervals) {
        if(intervals.length <=1) return intervals;

        Arrays.sort(intervals, (a,b) ->  a[0] - b[0] );

        ArrayList<int[]> list = new ArrayList<>();
        for(int[] range: intervals){

            if(list.isEmpty() || range[0] > list.get(list.size() -1)[1]){
                list.add(range);
            }else {
                list.get(list.size() -1)[1] = Math.max(list.get(list.size()-1)[1], range[1]);
            }

        }
        return list.toArray(new int[list.size()][]);
    }






    //------------------
    private static class RangeNode{
        int left;
        int right;
        RangeNode next;

        RangeNode(int l, int r){
            this.left = l;
            this.right = r;
        }
    }
    private RangeNode head = new RangeNode(-1, -1);

    public int[][] merge(int[][] intervals) {
        int len = intervals.length;
        for(int i=0;i<len;i++){
            int start = intervals[i][0];
            int end = intervals[i][1];
            RangeNode leftNode = null;
            RangeNode rightNode = null;

            RangeNode node = head;
            RangeNode previouNode = null;
            
            while(node!= null && start> node.right  ){
                    previouNode = node;
                    node = node.next;
            }
            if(node == null){
                previouNode.next = new RangeNode(start, end);
            }else{
                if(start<node.left && end< node.left){// new node
                    previouNode.next = new RangeNode(start, end);
                    previouNode.next.next =node;
                }else{ // merge node
                    leftNode = node;
                    if(start < node.left){
                        node.left = start;
                    }
                    while(node != null && end > node.right){
                        previouNode = node;
                        node = node.next;
                    }
                    if(node == null){// merge all
                        leftNode.right = end;
                        leftNode.next = null;
                    }else{
                        if(end < node.left){
                            leftNode.right = end;
                            leftNode.next = node;
                        }else if(node != leftNode){
                            leftNode.right = node.right;
                            leftNode.next = node.next;
                        }
                    }
                }

            }
            
        }
        
        int[][] result = new int[len][2];
        RangeNode tmp = head.next;
        int i=0;
        while( tmp != null){
            result[i][0] = tmp.left;
            result[i][1]= tmp.right;
            i++;
            tmp = tmp.next;
        }
        return Arrays.copyOfRange(result, 0, i-1);
    }
}

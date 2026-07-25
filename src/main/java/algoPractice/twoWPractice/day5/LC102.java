package algoPractice.twoWPractice.day5;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * https://leetcode.com/problems/binary-tree-level-order-traversal/  按层输出二叉树
 * LC102
 * 
 * BFS 是 Breadth-First Search ， 广度优先搜索或宽度优先搜索 ， 空间和时间都是复杂度为 $O(N)$
 */
public class LC102 {
    static class TreeNode{
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        } 
    }


    /**
     * 用 list 保留所有 node ，耗内存。
     * @param root
     * @return
     */
    public List<List<Integer>> levelOrder(TreeNode root) {
        if(root == null){
            return new ArrayList<List<Integer>>();
        }

        List<List<TreeNode>>  allNodes = new ArrayList<List<TreeNode>>();
        List<TreeNode> levelOne= new ArrayList<TreeNode>();
        allNodes.add(levelOne);
        levelOne.add(root);

        while(true){
            List<TreeNode> newLevel= new ArrayList<TreeNode>();
            List<TreeNode> lastLevel = allNodes.get(allNodes.size()-1);
            for(TreeNode node: lastLevel){
                TreeNode left = node.left;
                TreeNode right = node.right;
                if(left!=null)newLevel.add(left);
                if(right!=null)newLevel.add(right);
            }
            if(newLevel.size() > 0){
                allNodes.add(newLevel);
            }else{
                break;
            }
        }

        List<List<Integer>>  result = new ArrayList<List<Integer>>();
        for(List<TreeNode> list: allNodes){
            List<Integer> level= new ArrayList<Integer>();
            for(TreeNode node: list){
                level.add(node.val);
            }
            result.add(level);
        }

        return result;


    }


    /**
     * 深度优先搜索 (DFS)
     * @param root
     * @return
     */
    public List<List<Integer>> levelOrder2(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        iteNode(result, root, 0);
        return result;

    }

    private void iteNode(List<List<Integer>> result, TreeNode root, int index){
        if(root == null) return;
        if((index +1) > result.size()){
            result.add(new ArrayList<Integer>()) ;
        }
        List<Integer> level = result.get(index);
        
        level.add(root.val);
        if(root.left != null) iteNode(result, root.left, index +1);
        if(root.right != null) iteNode(result, root.right, index +1);
    }


    /**
     * BFS
     * 在面试中，遇到“层序遍历”或者最短路径问题，标准的做法是使用 队列 (Queue)。
     * 对于二叉树的层序遍历，核心技巧在于每次 while 循环开始时，先获取队列当前的 size，这个 size 就是当前层的节点总数。
     * 
     * 队列 FIFO， 进去多少，得到 size， 再出队这个 size 数量元素，同时不影响 offer 往队列尾部追加新元素，为下一轮准备。
     * @param root
     * @return
     */
    public List<List<Integer>> levelOrder3(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) return result;

        ArrayDeque<TreeNode> queue =  new ArrayDeque<>();
        queue.offer(root);

        int currentLevelSize = queue.size();
        while (currentLevelSize != 0) {
            List<Integer> currentLevelVal = new ArrayList<>();
            for(int i=0; i< currentLevelSize; i++){
                TreeNode node = queue.poll();
                currentLevelVal.add(node.val);
                if(node.left != null) queue.offer(node.left);
                if(node.right != null) queue.offer(node.right);
            }
            result.add(currentLevelVal);
            currentLevelSize = queue.size();
        }
        return result;

    }


}

package algoPractice.twoWPractice.day5;

/**
 * https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/description/
 * LC236
 * 找共同祖先，题目是最低一级的共同祖先，支持自己是自己的先祖，也就是说 如果 一个节点是另外一个节点的子孙节点，另外一个节点就是公共祖先。
 * 
 * 开始看题目时的担心：
 * 1， 查找到一个节点时，怎么查第二个节点？查到了也没有记住位置的地方，也往多次查询的岔路思考。
 * 2， 如果到了公共祖先节点后，后面的节点怎么传？
 * 
 * 破局点在： 
 * 1，查询节点时，可以同时比较两个目标节点，深度遍历的时候可以遍历到所有节点，这样两个节点都可以找到。
 * 2， 节点可以作为递归的返回值，左右一个有值就可以往上，到公共祖先节点时，左右必然都对应一个值。 当一个祖先节点时自身时，它的左右必然有一个是另外一节点。
 *     所以要支持一个值往上传。
 */
public class LC236 {

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    boolean bothFound = false;
    
    // p ，q 不一定存在于二叉树当中时， 查到了一个，还需查另外一个。
    public TreeNode correctONodePQNotSureExists(TreeNode root, TreeNode p, TreeNode q) {
        if(root == null || bothFound)return null;
        

        boolean isRoot = false;
        if(root == p || root == q){
            isRoot = true;
        }

        TreeNode l = correctONodePQNotSureExists(root.left, p, q);
        TreeNode r = correctONodePQNotSureExists(root.right, p, q);

        if(l != null && r != null) {
            bothFound = true;
            return root;
        }

        if(isRoot && (l != null || r != null)){
            bothFound = true;
            return root;
        }

        if(isRoot && l==null && r==null) return root;

        return l !=null ? l : r;

    }


    //p,q 一定存在于这个二叉树
    public TreeNode correctONodePQExists(TreeNode root, TreeNode p, TreeNode q) {

        //当前节点如果等于 p 或者 q， 如果他们是直接子孙关系，这个返回就是要找到结果，直接一路返回到树根节点(通过上层的左节点或者右节点)，然后出去作为结果。
        // 如果不是子孙关系，另外一个节点肯定在另外某个分支，同样一路上传，到达共同祖先那个节点时， 左右返回的各是这两个目标节点。那个节点就是要找到最小祖先节点。
        if(root == null || p == root  || q == root) return root; 

        TreeNode lf = null, rg= null;
        if(root.left != null) lf = correctONodePQExists(root.left, p, q);
        if(root.right != null) rg = correctONodePQExists(root.right, p, q);

        if(lf != null &&  rg != null) return root;

        //哪个分支不空就返回哪个分支结果，如果都为空就返回空。
        return lf != null ? lf : rg;
                
    }

    private TreeNode result;

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if(root == null || p == null  || q == null) return null;

        ancestor(root, p, q);

        return result;
        
    }



    private int ancestor(TreeNode root, TreeNode p,  TreeNode q){
        int a=0, b=0, c=0;
        if(root.equals(p) || root.equals(q)){
            a = 1;
        }
        if(root.left != null  ){
            b = ancestor(root.left, p, q);
        }

        if(root.right != null ){
            c = ancestor(root.right, p, q) ;
        }
        
        if(a ==1 && (b ==1 || c ==1))result = root;

        if(b==1 && c==1) result = root;

        return a +b +c;
    }
}

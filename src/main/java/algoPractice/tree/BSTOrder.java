package algoPractice.tree;


import java.util.LinkedList;

public class BSTOrder {
    public static void main(String[] args){

        BSTOrder bst = new BSTOrder();
        TreeNode<Integer> root=  bst.generateTree();
        bst.doPostOrder(root);
        bst.postOrder(root);

    }



    public void doInOrder(TreeNode<Integer> node){
        if(node == null)return;
        doInOrder(node.left);
        System.out.println(node.value);
        doInOrder(node.right);
    }

    public void doPreOrder(TreeNode<Integer> node){
        if(node == null)return;
        System.out.println(node.value);
        doPreOrder(node.left);
        doPreOrder(node.right);
    }

    public void doPostOrder(TreeNode<Integer> node){
        if(node == null)return;
        doPostOrder(node.left);
        doPostOrder(node.right);
        System.out.println(node.value);
    }

    public void preOrder(TreeNode<Integer> root){
        if(root == null)return;

        StringBuilder sb = new StringBuilder();
        LinkedList<TreeNode<Integer>> list = new LinkedList<>();
        list.push(root);

        while (!list.isEmpty()){
            TreeNode<Integer> node = list.pop();
            if(node.right != null)list.push(node.right);
            if(node.left != null)list.push(node.left);
            sb.append(node.value + ",");
        }

        System.out.println(sb);

    }

    //TOREViEW
    public void inOrder(TreeNode<Integer> root){
        if(root == null)return;

        StringBuilder sb = new StringBuilder();
        LinkedList<TreeNode<Integer>> list = new LinkedList<>();

        TreeNode<Integer> node = root;

        while (node != null || !list.isEmpty()){

            if(node !=null) {
                list.push(node);
                node = node.left;
            }else {
                node = list.pop();
                sb.append(node.value + ",");
                node = node.right;
            }
        }

        System.out.println(sb);
    }


    //TOREViEW
    public void postOrder(TreeNode<Integer> root){
        if(root == null)return;

        StringBuilder sb = new StringBuilder();
        LinkedList<TreeNode<Integer>> list = new LinkedList<>();

        TreeNode<Integer> node = root;

        TreeNode<Integer> popOne= null;

        while (node !=null || !list.isEmpty()){

            if(node != null){
                list.push(node);
                node = node.left;
            }else {
                //left child is empty, check right child
                node =  list.peek().right;
                if(node == null){
                    popOne = list.pop();
                    sb.append(popOne.value + ",");
                }else{
                    if(popOne == node){ // last time right child pop, no need process right child again.
                        popOne = list.pop();
                        sb.append(popOne.value + ",");
                        node = null;
                    }
                }


            }
        }
        System.out.println(sb);
    }

    //     2
//   1    4
//           9
//          7      20
//        5     16
//            12  18
    public  TreeNode<Integer> generateTree(){
        int[] arr = {2, 4, 1, 9, 20, 7, 5, 16, 12, 18};
        TreeNode<Integer> root = null;
        for(int a : arr){
            TreeNode<Integer> node = new TreeNode(a);
            if(root == null){
                root = node;
                continue;
            }

            TreeNode<Integer> target = root;
            while (target != null){
                if(node.value >= target.value){
                    if(target.right == null) {
                        target.right = node;
                        break;
                    }
                    target = target.right ;

                }else {
                    if(target.left == null) {
                        target.left = node;
                        break;
                    }
                    target = target.left;
                }
            }

        }
        return root;

    }


    private static class TreeNode<T>{
        TreeNode left;
        TreeNode right;

        T value;

        TreeNode(T value){
            this.value = value;
        }
    }
}





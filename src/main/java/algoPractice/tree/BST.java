package algoPractice.tree;

public class BST {

    private static class Node{
        private int value;
        private Node left;
        private Node right;

    }


    private Node build(Node node, int value){
        if(node == null){
            Node newNode = new Node();
            newNode.value = value;
            return newNode;
        }
        if(value <= node.value)node.left = build(node.left, value);
        else node.right = build(node.right, value);

        return node;
    }

    private void iterateTree(Node root){

        if(root == null) return;
        if(root.left != null) iterateTree(root.left);
        System.out.println( root.value);
        if(root.right != null) iterateTree(root.right);
    }


    public static void main(String[] args){
        BST bst = new BST();
        Integer[] array = { 20, 9, 10, 25, 16, 2, 40};
        Node root = null;
        for(int it : array){
             root = bst.build( root, it);
        }

        bst.iterateTree(root);

        System.out.println("----------------separator-------");
        bst.findNodesInRange(root, 9, 25);
    }


    void findNodesInRange(Node root, int left , int right){
        if(left > right ) throw new RuntimeException("left is bigger than right");

        if(root == null)return;
        if( root.value >= left && root.value <= right)System.out.println(root.value);

        if(root.value >= left) findNodesInRange(root.left, left, right);

        if(root.value <= right) findNodesInRange(root.right, left, right);



    }

}

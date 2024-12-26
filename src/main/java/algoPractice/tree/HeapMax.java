package algoPractice.tree;

import java.util.Arrays;


/**
 * 一棵深度为k的有n个结点的二叉树，对树中的结点按从上至下、从左到右的顺序进行编号，
 * 如果编号为i（1≤i≤n）的结点与满二叉树中编号为i的结点在二叉树中的位置相同，则这棵二叉树称为完全二叉树。
 */
public class HeapMax {

    public static void main(String[] args){
        HeapMax hm = new HeapMax();
        int[] arr= {22,5,66,1,40,27,4,55,68,49,18,17};
        hm.buildHeap(arr);

        for(int i= 0 ; i < arr.length; i++){
            hm.swap(arr, 0, arr.length -1 -i);
            hm.heapifyDown(0, arr, arr.length -1 -i-1);
        }

        Arrays.stream(arr).forEach(System.out::println);


        System.out.println(Integer.numberOfLeadingZeros(7));//29=32-3
        System.out.println(Integer.numberOfLeadingZeros(8));//28=32-4
        System.out.println(-1>>>29);
    }



    public void buildHeap(int[] arr){
        System.out.println(String.format("array length: %s", arr.length));

        //the last element of array index is (arr.length -1), it's parent is the last node who has child.  parent index
        // is  (arr.length -1 -1)/2
        int idx = (arr.length -1 -1)/2 ;

        // start from the last one who has child, each of his previous elements must have child as well. one by one.
        for(int i = idx; i >= 0; i--){
            heapifyDown(i, arr, arr.length -1);
        }
    }


    /**
     *
     * @param index
     * @param arr
     * @param limitationIdx which is used only when we want to utilize the array to save the max value which is just
     *                      removed from array.
     */
    public void heapifyDown(int index,  int[] arr, int limitationIdx){

        while ( (2*index + 1) <= limitationIdx){
            int leftIdx = 2*index + 1;
            int rightIdx = 2*index + 2;

            int largerIdx = leftIdx;
            if(rightIdx <= limitationIdx && arr[rightIdx] > arr[leftIdx]){
                largerIdx = rightIdx;
            }
            if(arr[largerIdx] <= arr[index]) break;
            swap(arr, index, largerIdx);
            index = largerIdx;
        }

    }

    public void heapifyUp(int index,  int[] arr){
        int parentIdx = (index - 1)/2;

        while (parentIdx > 0){
            if(arr[index] <= arr[parentIdx])break;
            swap(arr, index, parentIdx);
            index = parentIdx;
            parentIdx = (index - 1)/2;
        }

    }

    public void swap(int[] arr, int idx1, int idx2){
        if(idx1 >= arr.length || idx2 >= arr.length || idx1 < 0 || idx2 < 0){
            throw  new RuntimeException("index is out of bound.");
        }
        int temp = arr[idx1];
        arr[idx1] = arr[idx2];
        arr[idx2] = temp;
    }


}

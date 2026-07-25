package algoPractice.twoWPractice.day4;

import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;



/**
 * 先给最优理论解（展现算法功底）：向面试官阐述 Quick Select（快排划分思想），指出其平均时间复杂度可以达到 $O(N)$，空间复杂度为 $O(1)$。
 * 然后手写双指针 Partition 逻辑。（注意：基准点必须引入随机数，否则遇到极端降序数组，时间复杂度会退化成 $O(N^2)$）。
 * 再给工业工程解（展现架构思维）：主动向面试官补充：“虽然 Quick Select 理论上是 $O(N)$，但在分布式、海量大数据或流处理场景下，数据无法一次性加载到内存。
 * 此时，我会选择使用容量为 $k$ 的最小堆（PriorityQueue）。它不仅空间复杂度只有 $O(k)$，而且非常适合做 Top-K 的流式计算，是实际架构中的首选。”
 * LC215
 */

public class LC215 {


    // topK 最小堆实现, 流式首先，因为不能把所有数据加载在数组里，也太费内存。
    public int findKthLargestWithHeap(int[] nums, int k) {

        PriorityQueue<Integer> queue = new PriorityQueue<Integer>(k);
        for(int el:nums){

            if(queue.size()<k){
                queue.add(el);
            }else{
                Integer top = queue.peek();
                if(el> top){
                    queue.poll();
                    queue.add(el);
                } 
            }
        }
        
        if(queue.size()!=k)return Integer.MIN_VALUE;


        return queue.poll() ;
    }


    //用Quick Select 分区的理论时间复杂度是惊人的 O(N)，比堆的 O(N log k) 还要快！
    //第一次 N + 第二次 N/2+  第三次 N/4 +.... = 2N = O(N) ,因为不用真正的排序，直接丢掉一半。 


    /**
     * 快排能每次定位一个位置，这个位置上的数能确定。左边和右边值可以没有完全排序，但是左边是小于等于， 右边是大于等于。
     * 我们要找第 K大，不就是想知道哪个数能刚好死死坐在 nums.length - k 这个索引上吗？所以能用快排思想，随机找一个数，分开数组，这样就能定一个位置，
     * 再根据这个位置和nums.length - k相对比，缩小数组范围，直到确定nums.length - k位置的数。
     * 
     * 
     * 随机挑数组里一个数，分割数组时候有两种方式，我们把这个数叫做 pivot，pivot 可以不参加前期移动，等定好位置了再进去。另外一种是pivot 参加移动，只是分割数组，它在哪里不知道。
     * 第一种方式是pivot 选定了后， 把它放在数组头或者尾部，也就是把头或者尾和 pivot 位置数据互换。
     * 这样只用处理去头或者尾的数组就好了，等确定了pivot 割裂数组的位置后再把这这个位置的数和 pivot 数据（头或尾）互换。pivot 最后归位，所以叫归位算法。
     * 第二种是霍尔划分（Hoare Partition）， 每次把数组分成两份，一份大于等于 pivot， 另外一份小于等于 pivot， 至于 pivot 数在哪一部分不用关心。再看nums.length - k 在哪个数组
     * 当中，选这个数组再去切分。因为第一种把 pivot 先独立出来了，其他数肯定能分成一份或者两份。 假设数组值都相同，这样每次只是能切出一个数 pivot。
     * 而第二种方式中，假设数组值都相同，如果左右 l++/r-- 判断中 while 任何一方包含了等号，就会出现 l到底 或者r到初始位置，l=r 切分结束的结果还是原来数组，就是循环了。
     * 所以霍尔划分实现的时候用 do l++/r-- while 大于或者小于，没有等于，如果数字是和 pivot 数相等，左右交换，下一轮它 用 do l++/r-- while 就跳出了。霍尔划分实现起来边界条件
     * 更难理解，自己实现优先用第一种方式。
     * 
     * 
     */


    /**
     * 把随机数 pivot 保存起来的方式。切成三份。[] + pviot + []
     * @param nums
     * @param k
     * @return
     */
    public int findKthLargest_1(int[] nums, int k) {
        int start=0, end=nums.length -1;
        int p = nums.length - k;  // 如 10 个数字数组， 第4大就第七个元素，下标是 6.

        while (start < end) {
            int pivotIndex = partition(nums, start, end);
            if(p==pivotIndex)return nums[pivotIndex];
            else if(p > pivotIndex){
                start = pivotIndex+1;
            }else{
                end = pivotIndex -1;
            }
        }

        return nums[start];

    }   

    /**
     * 基准值（Pivot）在哪边，就让相反方向的指针先走。Pivot 藏在左边（start），就让右指针（$r$）先走。Pivot 藏在右边（end），就让左指针（$l$）先走。
     * 按照这个口诀写，相遇点永远是安全的，就不需要写任何后置的 if 纠偏逻辑，代码会更干净！
     * 下面实现带有纠偏
     * @param nums
     * @param start
     * @param end
     * @return
     */

    private int partition(int[] nums, int start, int end){
        int rd = ThreadLocalRandom.current().nextInt(start, end+1);
        int pivot = nums[rd];
        swap(nums, start, rd); // 第一位放 pivot，原来第一位的数赋值到原 pivot 位置。

        int l = start, r=end; //start 会被 l++
        while(l<r){
            //先移动 l， 找比 pivot 大的数的位置，也有可能加到 r，l=r 了，r 就减不了, l 的数肯定 >= pivot. 如果最后一个数也小于 pivot，r 位置下面数小于了 pivot
            while(nums[l] <= pivot && l<r)l++;

            //后移动 r， 找比 pivot 小的数的位置，也可能直接减到 l，l=r了，比如第一个位置就是比 pivot 大的数，后面也是。
            while(nums[r] >= pivot && l<r) r--;

            if(l<r)swap(nums, l, r); // 交换后，l 会在下一轮先往前，如果等于 r 也就加不了，r 也减不了，l还是和 r 相等

        }

        //上一步交换后，l,r种如果没有隔一个，l 先进一步，lr 相等；有元素l 能过的话还会过到 r， 不能过证明这个数比 pivot 大，r 能减到 l，还是相等。
        //所有情况都是 l=r， r 这位置数一定>pivot, 即使最小的数在再开始，等于 pivot， 但是 l 先于 r 移动。所以 r 还是到不了。
        
        if(nums[l] > pivot)l--;
        swap(nums, start, l);
        return l;


    }


    

    private void swap(int[] nums, int a, int b){
        int tmp = nums[a];
        nums[a] = nums[b];
        nums[b] = tmp;
    }


    public int findKthLargest_2(int[] nums, int k) {
        int start=0, end=nums.length -1;
        int p = nums.length - k;  // 如 10 个数字数组， 第4大就第七个元素，下标是 6.


        while(start < end){
            int l= start -1, r = end +1 ; //兼容do while

            int rd = ThreadLocalRandom.current().nextInt(start, end+1);
            int pivot = nums[rd];
            while (l<r) {
                
                do {l++;} while(nums[l] < pivot);// pivot 在数组中，即使最大最后一位，l 只能到达最后一位，不能++了
                do {r--;} while(nums[r] > pivot);
    
                if(l<r) {
                    swap(nums, l, r);
                }
            }
            //l>r, r 下标 数<= pivot
    
            if(p > r){
                start = r+1;
            }else{
                end = r;
            }
        }
        return nums[start];


    }
    
}

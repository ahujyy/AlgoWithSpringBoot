package algoPractice.DP;

public class LongestSubSequence {


    /**
     * Given an integer array nums, return the length of the longest strictly increasing
     * subsequence
     *
     * Example 1:
     *
     * Input: nums = [10,9,2,5,3,7,101,18]
     * Output: 4
     * Explanation: The longest increasing subsequence is [2,3,7,101], therefore the length is 4.
     * Example 2:
     *
     * Input: nums = [0,1,0,3,2,3]
     * Output: 4
     * Example 3:
     *
     * Input: nums = [7,7,7,7,7,7,7]
     * Output: 1
     *
     * @param args
     */
    public static void main(String[] args) {
        LongestSubSequence subSequence = new LongestSubSequence();
        //int nums[] = {10,9,2,5,3,7,101,18};
        //int nums[] = {4,10,4,3,8,9};
        //
        int nums[] = {3,5,6,2,5,4,19,5,6,7,12};
        System.out.println(subSequence.lengthOfLIS_NXLOGN(nums));
    }

    // create one array which contains each length subsequence's (0~ current longest subsequence) smallest top value.
    // when a next value comes,
    // 1, if it's bigger than current longest subsequence top value, then longest subsequence increase by 1,
    // and this value is the latest longest subsequence's smallest top value.
    // 2, if it's smaller than current longest subsequence top value, we need find its position of the array we created,
    // this array is ordered ,so can use binary search. the reason why we need update the accurate value of each length
    // subsequence smallest top value  is  new coming value (smaller than the longest top value ) may establish a longest
    // subsequence . like when last value 6 comes in numbers : 1, 101, 102, 103, 104, 2, 3, 4, 5, 6.
    public int lengthOfLIS_NXN(int[] nums) {
        if(nums == null || nums.length ==0) return 0;

        int longestSeq[] = new int[nums.length];

        int maxLen = 1;
        for(int i=0; i< nums.length; i++){
            longestSeq[i] =1;

            for(int j = 0; j< i; j++){
                if(nums[i] > nums[j] && (longestSeq[j] + 1) > longestSeq[i] ){
                    longestSeq[i] = longestSeq[j] + 1;
                }
            }
            if(longestSeq[i] > maxLen) maxLen = longestSeq[i];
        }
        return maxLen;
    }

    public int lengthOfLIS_NXLOGN(int[] nums){
        if(nums == null || nums.length == 0) return 0;

        //index means the smallest top value of the subsequence with length index +1
        int smallestTopValueOfSeqWithLen[] = new int[nums.length];

        int maxSqLength = 1;
        for(int i=0 ; i< nums.length; i++){
            if(i == 0){
                smallestTopValueOfSeqWithLen[0] = nums[i];
                continue;
            }

            if( nums[i] > smallestTopValueOfSeqWithLen[maxSqLength -1]){
                maxSqLength ++;
                smallestTopValueOfSeqWithLen[maxSqLength -1] = nums[i];
            } else {

                int left = 0, right = maxSqLength -1;
                while ( left < right){
                    int mid = (right + left ) /2;
                    if( nums[i] < smallestTopValueOfSeqWithLen[mid]){
                        right = mid -1;
                    }
                    else if( nums[i] > smallestTopValueOfSeqWithLen[mid]) left = mid +1;

                    else break; // equal

                }

                // when sum of left and right divides 2, result may equal to left, be impossible to be equal to right.
                // edge cases:
                //  1, nums[i] may be smaller than smTVOSWithLen[left], last time of loop: right = mid -1, [mid = left]
                //  2, nums[i] may be bigger than smTVOSWithLen[right], last time of loop: left = mid +1 , [mid = left = right -1]
                //
                if( (left == right) ||  (right == left -1) ){ // not equal break
                    if(nums[i] < smallestTopValueOfSeqWithLen[left]) smallestTopValueOfSeqWithLen[left] = nums[i];
                    else if(nums[i] > smallestTopValueOfSeqWithLen[left]) smallestTopValueOfSeqWithLen[left +1] = nums[i];
                }
            }
        }

        return maxSqLength;

    }



}

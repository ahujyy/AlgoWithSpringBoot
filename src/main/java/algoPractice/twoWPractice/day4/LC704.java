package algoPractice.twoWPractice.day4;

/**
 * https://leetcode.com/problems/binary-search/
 * LC704
 */
public class LC704 {
    public int search(int[] nums, int target) {
        int len = nums.length;
        if(len == 0) return -1;
        int left=0, right=len-1;
        while(left<=right){
            int mid = (left+right)/2;
            if(target > nums[mid]){
                left = mid+1;
            }else if(target < nums[mid]){
                right = mid-1;
            }else{
                return mid;
            }
        }
        return -1;
    }
}

package algoPractice.DP;

/**
 * You are a professional robber planning to rob houses along a street.
 * Each house has a certain amount of money stashed,
 * the only constraint stopping you from robbing each of them is that adjacent houses have security systems connected
 * and it will automatically contact the police if two adjacent houses were broken into on the same night.
 *
 * Given an integer array nums representing the amount of money of each house,
 * return the maximum amount of money you can rob tonight without alerting the police.
 *
 * Input: nums = [2,7,9,3,1]
 * Output: 12
 * Explanation: Rob house 1 (money = 2), rob house 3 (money = 9) and rob house 5 (money = 1).
 * Total amount you can rob = 2 + 9 + 1 = 12.
 */
public class HouseRobber {

    public static void main(String[] args){
        HouseRobber houseRobber = new HouseRobber();
        int[] arr = {2,7,9,3,1};
        System.out.println(houseRobber.rob(arr));
    }

    public int rob(int[] nums) {

        int length = nums.length;
        if(length ==1)return nums[0];
        if(length == 2) return Math.max(nums[0], nums[1]);


        int previousRob = nums[1], previousNotRob = nums[0];
        for(int i = 3; i <= length; i++){

            int rob = nums[i -1] + previousNotRob;
            int notRob = Math.max(previousNotRob, previousRob);
            previousRob =rob;
            previousNotRob = notRob;
        }

        return Math.max(previousNotRob, previousRob);

    }
}

package algoPractice.twoWPractice.day2;

import java.util.HashMap;

public class LC1 {
    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
        int index = 0;
        for (int num:nums){
            if(indexMap.containsKey(num)){
                int[] result = {indexMap.get(num), index};
                return result;
            } 

            int diff = target - num;
            indexMap.put(diff, index);
            index++;
        }
        return null;
    }
}

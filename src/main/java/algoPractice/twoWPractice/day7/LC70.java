package algoPractice.twoWPractice.day7;

/**
 * https://leetcode.com/problems/climbing-stairs/
 * LC70
 */
public class LC70 {
    public int climbStairs(int n) {
        if(n <= 2)return n;
        int[] steps = new int[n+1];
        steps[0] = 0;
        steps[1] = 1;
        steps[2] =2;
        int i=3;
        while(i<=n ){
           steps[i]= steps[i-2] + steps[i-1];
           i++;
        }
        return steps[n];
    }
}

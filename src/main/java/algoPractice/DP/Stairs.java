package algoPractice.DP;


/**
 * You are climbing a staircase. It takes n steps to reach the top.
 *
 * Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?
 */
public class Stairs {

    public static void main(String[] args){

        Stairs stairs = new Stairs();
        System.out.println(stairs.climbStairs(45));

    }

    public int climbStairs(int n) {

        if(n ==1 )return 1;
        if(n ==2 )return 2;

        //return climbStairs(n-1) + climbStairs(n-2);
        int previousTwo = 1;
        int previousOne = 2;
        int result = 0;
        for(int i = 3; i<=n ;i ++){
            result = previousOne + previousTwo;
            previousTwo = previousOne;
            previousOne = result;
        }

        return result;
    }
}

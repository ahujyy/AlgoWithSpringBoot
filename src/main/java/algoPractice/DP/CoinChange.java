package algoPractice.DP;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CoinChange {
    public static void main(String[] args) {
        CoinChange coinChange = new CoinChange();

    }
    public int coinChange(int[] coins, int amount) {

        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 0);


        for(int i=1; i <= amount; i++){
            int cnt = Integer.MAX_VALUE;
            for(int coin: coins){
                int start = i-coin;
                if(start >= 0 && map.containsKey(start)){
                    if(map.get(start) + 1 < cnt)cnt = map.get(start) +1;
                }
            }

            if(cnt != Integer.MAX_VALUE){
                map.put(i, cnt);
            }
        }

        Integer res = map.get(amount);
        return res==null ? -1 : res;

    }

    public int coinChange2(int[] coins, int amount) {
        int[] arr = new int[amount+1];
        arr[0] = 0;


        for(int i=1; i <= amount; i++){
            int cnt = Integer.MAX_VALUE;
            for(int coin: coins){
                int start = i-coin;
                if(start == 0  || (start > 0 && arr[start] != 0) ){
                    if(arr[start]  + 1 < cnt)cnt = arr[start] +1;
                }
            }

            if(cnt != Integer.MAX_VALUE){
                arr[i] = cnt;
            }
        }

        if(amount == 0)return 0;

        return  arr[amount] == 0 ? -1 : arr[amount] ;
    }


    public int coinChange3(int[] coins, int amount) {
        int max = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, max);
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            for (int j = 0; j < coins.length; j++) {
                if (coins[j] <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }


}

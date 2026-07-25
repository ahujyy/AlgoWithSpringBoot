package algoPractice.twoWPractice.day7;

/**
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock/
 * LC121
 *  
 * 股票价格找利润最大化解， 假设某天卖掉利润最大， 那这天N的价格是 P，结果就是求0～N-1 天内价格最小值。问题简化为维护一个区间的最小值。
 * 
 */
public class LC121 {


    public int maxProfit(int[] prices) {
        int max = 0;
        if(prices.length <=1)return max;
        int index = 0;
        for(int i=1; i< prices.length; i++){
            if(prices[i] < prices[index]){
                index = i;
            }else{
               int pro = prices[i] - prices[index];
                if(pro>max)max= pro;
            }
        }
        return max;
    }


    public int maxProfit_slow(int[] prices) {
        int max = 0;
        if(prices.length <=1)return max;
        int[] pro = new int[prices.length];
        pro[0] = 0;
        for(int i=1; i< prices.length; i++){
            for(int j= 0; j<i; j++){
                if(prices[i] > prices[j]){
                    int tmp = pro[j] + prices[i] - prices[j];
                    if(tmp > pro[i]) pro[i] = tmp;
                }
            }
            if(pro[i]>max) max=pro[i];
        }
        return max;

    }
}

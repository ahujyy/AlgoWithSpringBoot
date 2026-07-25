package algoPractice.twoWPractice.day8;

/**
 * https://leetcode.com/problems/multiply-strings/submissions/2066180309/
 * LC43
 * 
 * 不能用java 内嵌的 api 直接把 string parse 为 Integer。 怎么算相乘。
 * string 太长，转化会溢出。 模拟竖式乘法来解决。 长度 m 和 n 的数相乘， 最大长度为 m+n。
 * 结果长度不够的时候注意结尾要去掉开头的0.
 * 下标 a 和下标 b 的数相乘， 结果放在位置 a+b+1， 10 的倍数要进点。
 * 
 */
public class LC43 {
    public String multiply(String num1, String num2) {

        int m = num1.length();
        int n = num2.length();

        // 长度 m的数 * 长度 n 的数 的结果最大长度为 m+n
        int[] result = new int[m+n];
        
        //num1 * num2 , 用 num2 的每一位去乘num1 的每一位
        for(int i=n-1; i>=0; i-- ){
            int v2 = num2.charAt(i) - '0';
            for(int j=m-1; j>=0; j--){
                int v1 = num1.charAt(j) - '0';
                int multiple = v2 * v1;
                int sum = result[i+j+1] + multiple;
                result[i+j+1] =  sum  % 10;

                result[i+j] = result[i+j] + sum/10;

            }
        }
        StringBuilder sb = new StringBuilder();

        
        for(int l=0; l< m+n; l++){
            if(!(sb.length()==0 && result[l]==0))
            sb.append(result[l]);
        }
        
        return sb.toString().length() ==0 ? "0" :  sb.toString() ;
        
    }
}

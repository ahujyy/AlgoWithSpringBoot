package algoPractice.twoWPractice.day8;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * 真前缀：包含首字符，但不能包含尾字符（因为）
 * 真后缀：包含尾字符，但不能包含首字符
 * 
 * 主串： 要在此串中找匹配的模式串的子串。
 * 模式串： 也就目标子串。在主串当中找它。
 * 
 * 如果模式串和主串匹配的部分中没有重叠的子串，那就直接跳过这个匹配部分往后匹配，只有重叠部分才需要计算，这个计算就核心。
 * 例如：
 * 主串：  ABCDEFGIJK
 * 模式串: ABCDY
 * ABCD 匹配，主串的位置来到下标 4（E），不动， 模式串匹配串 ABCD 里面没有重叠的真前缀和真后缀的，所以模式串直接跳到 4 的位置。
 * 假设不能跳，可以直接尝试主串的 B ，C，D位置， 如果模式串头分别和他们对齐，有匹配，那么意味着匹配的部分往前走到主串匹配开始的位置（真后缀）等于 
 * 模式串匹配部分的真前缀，违反这个模式串没有重叠子串的事实。 
 * 
 *
 * 
 * 
 * 
 * 匹配就是模式串头和主串某个下标对齐，看后续能不能匹配， 如果哪一位不匹配（也许模式串的头，或者头的下一位， 或者。。直到模式串的最后一位），两者都相同的那个地方
 * 在主串上表现的指针不回退， 那要模式串要往前挪多少步呢？ 这就要找模式串自身的重复串。
 * 模式串不匹配的位置可以从 0 到 len-1,   如果下标 n 不匹配，也就是说 0～n-1 是匹配的，就要算 0～n-1 的这个串真前缀和真后缀相等最大长度。 每个位置的值是PMT。
 * 
 * 手搓 PMT   设个模式串 为 ABCDABCE. （为什么算每个位置，因为开始不匹配 （不等）的位置前都匹配，就是看这部分怎么移动，如果有这部分里面有重叠，就可以直接跑到重叠的地方。
 * 位置 0。 子串为 A。  真前缀和真后缀都没有。 值就为 0
 * 位置 1， 子串为 AB， 真前缀A, 真后缀 B， 不匹配。值为 0
 * 位置 2， 子串为 ABC， 真前缀:A,AB ----真后缀：C, BC. 长度一样的不匹配。值为 0
 * 位置 3， 子串为 ABCD， 真前缀:A, AB ,ABC.   真后缀：D， CD， BCD。 长度一样的不匹配。 值为 0
 * 位置 4， 子串为 ABCDA， 真前缀:A, AB ,ABC,ABCD 真后缀：A,DA, CDA, BCDA. 长度一样只有 A匹配， 有一个 A 长度为 1， 值就为 1
 * 位置 5， 子串为 ABCDAB，  真前缀:A, AB ,ABC,ABCD,ABCDA  真后缀：B,AB, DAB, CDAB， BCDAB.  只有 AB 相等。 值为 2
 * 位置 6， 子串为 ABCDABC，  真前缀:A, AB ,ABC,ABCD,ABCDA,ABCDAB.  真后缀：C, BC, ABC, DABC, CDABC, BCDABC. 就 ABC 等 值为 3.
 * 位置 7， 子串为 ABCDABCE, 真前缀:A, AB ,ABC,ABCD,ABCDA,ABCDAB,ABCDABC, ABCDABCE.  真后缀: E, CE, BCE, ABCE, DABCE, CDABCE, BCDABCE. 没有相等。值为 0.
 * 
 * 位置 6 的值为 3 的意思是：如果 ABCDABC都匹配，到了 E 开始和主串不匹配，  假设 E 的位置主串为 n, 第一个A的位置是n-7. 
 * 匹配的长度是7, 位置 6 重叠串长度为 3（ABC）， 模式串移动的距离就是 7-3=4 （从 n-7 跑到 n-3）. 主串还是 n ，然后比较 n 位置及以后。
 *  n-7, n-6, n-5, n-4, n-3, n-2, n-1, n 
 *  A    B    C    D    A    B    C    D  A  Z  F 主串
 *  A    B    C    D    A    B    C    E         模式串
 *                      A    B    C    D  A  B  C  
 * 
 * 
 * KMP
 */
public class KMP {


    public static void main(String[] args) {
        KMP kmp = new KMP();
        String mainStr = "ABABA";
        String modelStr = "ABA";
        System.out.println("匹配到的起始下标: " + kmp.match(mainStr, modelStr));    
    }


    public ArrayList<Integer> match(String mainString, String modelString){

        if(null == mainString || null == modelString || mainString.length()==0 || modelString.length()==0)return new ArrayList<>();
        int len = mainString.length();
        int mdLen = modelString.length();
        int[] next = getPMT(modelString);
        int i=0, j=0;

        ArrayList<Integer> startIndex =  new ArrayList<>();

        while(i<len){
            if(mainString.charAt(i) == modelString.charAt(j)){
                i++;
                j++;
                if( j == mdLen ){
                    j = next[j-1]; // 匹配完全成功，j 回到最后一个字符的真前后缀长度值（也是将要比较的下一个位置）
                    startIndex.add(i-mdLen);
                }
            }else if(j==0 && next[j] == 0){
                i++;
            }else{
                j = next[j-1];  // j 的位置和 i位置不相等， 但是 [0 ～ j-1]是匹配过的，所以要取这个匹配过最后一位的 PMT 值。
            }
        }


        return startIndex;
    }


    private int[] getPMT(String modelString){// 找每个位置 i的相等真前后缀长度，如果长度为 l， 也就是 0 ～ l-1 相等，l 就是下一个要比较的位置。
        int len = modelString.length();
        int[] next = new int[len];

        int j=0;
        for(int i=1; i<len; i++){
            
            // 新位置（i上一轮++了），和上一轮结果j（长度， 假设是 j= 5，业绩是说上轮位置上modelString [0 ～ 4] 和真后缀相等，j =5 正好在第六个位置，
            // 也就是这轮要比较位置，如果相等j++，也就是匹配的长度又增加一位。不相等，也就上一轮的真后缀+[i]位置 ！= 上轮真前缀+[j]位置， 
            // 就退而求其次，因为上轮真后缀=上轮真前缀， 求其次就去看 上轮真后缀的真后缀+[i]位置  ?==(是否=)  上轮真前缀的真前缀+ [j]  ,
            // 其实就是右后边的串（上轮真后缀）的前面去除一定位数， 左前边的串(上轮真前缀)的后边去除掉一定位数 加上各自的 i， j 位置看是否相等。
            // 因为上轮前后缀相等，所以就是求上轮真前缀的真前后缀，上轮真前缀串的最后位置是 j-1, 这个值正好保存在 next[j-1]中，这利用了动态规划。
            // j=0的时候就只能比较 j 位置和 i 位置了，不等的时候就是初始值 0，等的话就是 j++ 后 的 1.
            while(modelString.charAt(i) != modelString.charAt(j) && j>0 ){
                j = next[j-1];
            }

            if(modelString.charAt(i) == modelString.charAt(j)){
                j++;
            }
            next[i] = j;
            
        }
        return next;


    }
}

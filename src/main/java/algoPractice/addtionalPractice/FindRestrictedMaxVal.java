package algoPractice.addtionalPractice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;



/**
 * 给定一个数字串 和 一个有限个数字集合， 求新数必须由有限数字集合里的数字构成，此数比给定的数字串要小，但是是结果集中最大的。
 * FindRestrictedMaxVal
 */

public class FindRestrictedMaxVal {

    public static void main(String[] args) {
        

        String abc = "45371";
        char[] resValAr = {'3', '2','5'};

        // asList(T... a) 泛型 T 是一个 Object ， primitive type 不是对象，所以传char[]或者int[] 会把 T 解析成char[]或者int[],
        // 但是如果是对象型，比如 传 String[], T 才会解析成 String
        // List<char[]> oneElementList =  Arrays.asList(resValAr);

        // Arrays.sort(resValAr); 可以对char[] 进行升序排序。

        // expect: 35555   / 3 2 7 ---37777 /4 7 9  --44999  ：下面错误代码， 正确代码见基于 dfs 的backtracking
        var resList = new ArrayList<Character>();
        for(char c : resValAr){
            resList.add(c);
        }

        resList.sort(Comparator.comparing(a->a));
        char maxChar = resList.stream().max((a,b)-> a.compareTo(b) ).orElse(null);
        System.out.println("max digits: " + maxChar);

        StringBuilder sb = new StringBuilder();
        boolean useLessV = false;

        
        for(char c : abc.toCharArray()){

            if(useLessV){
                sb.append(maxChar);
                continue;
            } 

            char chooseC = 'a';
            for(char t: resList){
                if(c == t){
                    chooseC = t;
                }else if( c < t) {
                    useLessV  = true;
                    break;
                }else{
                    chooseC = t;
                }
            }
            if(chooseC == 'a'){
                throw new RuntimeException("cat not find " + c + " in  " +resList);
            }
            sb.append(chooseC);

        }


        System.out.println(sb.toString());


        System.out.println("---------------------------------------------" );
        FindRestrictedMaxVal inst = new FindRestrictedMaxVal();
        char[] test1 = {'5', '7', '2'};
        inst.backtracking("84327", test1);

        inst.backtracking("24327", test1);

        inst.backtracking("21111", test1);

    }

    /**
     * 回溯法通常与 DFS (Depth-First Search，深度优先搜索) 结合使用.
     * 此题的主要思想是，将当前的数字与受限制范围内的数组所有数字进行比较，在里面找到小于等于当前数字，
     * 如果前面一个数使用了比它小的数，那当前用来比较的数字从当前数变成9。因为 9 是 0~9 里面最大的一个数，也就是说，比较的结果必定是受限数组里的最大数。
     * 这里要考虑回溯的原因是，你当前假设找到了一个小于等于比较数的数字，但这并不保证这个数后面的数字也能在受限数组里面找到合适的数。
     * 如果后面一个都找不到，也就是说后面的数取不到值了，那它前面的数就要重新找，即回溯，找一个比原来值更小的合适的数。
     *  如果能找到，那后面所有的数就是取受限数组中最大的数了。如果前面的数也找不到合适的数，那接着就往前面的前面找。如果一直回溯到第一个数，第一个数也找不到合适的数了，
     * 那就证明这种和原数有同样位数的最大的比它小的数不存在，那就要找比原数小一位的最大数。 例如 “22221” / {2,3} ==>"3333" , 中间能找到的例子"22321"/{2,3}==>"22233"
     * 这里回溯到上一层，也就是重新处理上一个数字，重新从受限数组里找到一个比原先值更小的数，在代码里就对应着循环遍历“受限受阻”里面从大到小排序的值。
     * 
     * // 一层循环 //
     *       boolean 反馈，不行就下一个迭代<-----//二层循环//
     *                                          boolean 反馈<-----//三层循环//    
     *                                                              boolean 反馈<-----//四层循环//   
     */          

    private void backtracking(String numbeString, char[] scope){
        
        Arrays.sort(scope); // 从小到大排序

        StringBuilder sb  = new StringBuilder();
        boolean rootSuccess = dfs(numbeString, scope, 0, true, sb);
        if(rootSuccess){
            System.out.println("找到和"+ numbeString +"同长的比它小的最大数为：" + sb.toString());
        }else{
            for(int i=0; i< numbeString.length()-1; i++){
                sb.append(scope[scope.length -1]);
            }
            System.out.println("找到比"+ numbeString +"长度小一的比它小的最大数为：" + sb.toString());
        }

    }

    /**
     * 
     * @param numbeString
     * @param scope
     * @param index  待处理的数字在字符串中的 index
     * @param isLimit false 的时候表示前面一个数字取了比它自身小的数，后续的位数上的数取 scope 里最大的数就可以了。
     * @return
     */
    private boolean dfs(String numbeString, char[] scope, int index, boolean isLimit, StringBuilder sb ){

        if(index == numbeString.length())return true; //已经找完了个位数上的数字了。

        char digit = numbeString.charAt(index);
        char comparingChar = isLimit ? digit : '9';

        for(int i= scope.length -1; i>=0; i--){ // 从最大开始找小于等于comparingChar的数字
            if(scope[i] <= comparingChar){
                sb.append(scope[i]);
                boolean newLimit = isLimit && (scope[i] == comparingChar);
                if(dfs(numbeString, scope, index+1, newLimit, sb)){ // 后面的数都反馈都能在 scope 里找到合适的数。
                    return true;
                }else{ // 后面位数的数反馈找不到，回溯到当前，撤销前面的决定，接着循环在 scope 里找次小数。
                    sb.deleteCharAt(sb.length()-1);
                }
            }
        }
        
        // 遍历完scope[] 都没有找到， 那就要回溯上一层，返回 false 反馈上去。
        return  false;

    }


   
}

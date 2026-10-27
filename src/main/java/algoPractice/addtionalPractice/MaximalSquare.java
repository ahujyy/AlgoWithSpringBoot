package algoPractice.addtionalPractice;


/**
 * 
 * https://leetcode.com/problems/maximal-square/
 * 
 * Given an m x n binary matrix filled with 0's and 1's, find the largest square containing only 1's and return its area.
 * 注意点：
 * 1， leetcode 上面 matrix保存的是 char 数组， 优化计算的时候数组里保存不了10 以上的10 进制值。 '0'+9 可以是 '9', '0'+10 就是符号了，这时候得单独建一个 int[][]
 * 2， 返回的结果要面积
 * 3， 暴力解题的时候，得注意节点值为1， 就要看面积最大长度还是不是 0，是 0 要设成 1.
 * 
 * MaximalSquare
 */
public class MaximalSquare {


    public static void main(String[] args) {
        MaximalSquare instSquare = new MaximalSquare();
        // int[][] matrix = {
        //     {1, 0, 1, 0, 0},
        //     {1, 0, 1, 1, 1},
        //     {1, 1, 1, 1, 1},
        //     {1, 0, 0, 1, 0}
        // };

        int[][] matrix = {
            {0, 1},
            {1, 0}
     
        };
        
        //System.out.println(instSquare.ms(matrix));
        System.out.println(instSquare.msOptimal(matrix));
    }


    private int ms(int[][] matrix){
        int result = 0;
        int len = matrix.length;
        int colLen = matrix[0].length;
       
        for(int i=0; i< len; i++){
            for(int j=0; j < colLen; j++){
                
                if(matrix[i][j] ==1 ){

                    if(1>result)result=1; //  need to init square length to 1 if one element is digit "1".
                    int minLen = Math.min(len - i-1, colLen -j-1); // Get the maxium avavible square extention steps .  
                    boolean isBreak = false;
                    for(int x=1; x<=minLen; x++){ // for each step to extend square by directing to down and right.

                        // check new-join row (i is fixed, to increase j)
                        for(int y=0; y<=x; y++){
                           if( matrix[i+x][j+y] == 0 ){
                                isBreak = true;
                                break;
                           }
                        }
                        if(!isBreak){
                            //check new-join column (j is fixed, to increase i)
                            for(int m =0; m <= x; m++){
                                if(matrix[i+m][j+x] == 0){
                                    isBreak = true;
                                    break;
                                }
                            }
                        }

                        int best = !isBreak ? x +1 : x; // new join-row and new join-column pass, then sqaure successfully extend to x+1 (length)
                        if(best > result)result = best;

                        if(isBreak) break; // any break means no need for next round extension.
                    }
                }
            }
        }
        return result * result; // need to return the square.

    }


    /**
     * 每个 element 做一次以它为正方形的右下角，看最大的正方形是多大。
     * 如果能成功为正方形， 要满足：
     * 1， 本身 element 的值为 1， 也就是自身是长度为 1 的正方形，如果值不为 1， 它自身就不能成为正方形的一角。
     * 2， 条件1 满足后， 接着就是往左上角的对角线往外扩张， 它相邻的对角 element 就必须满足： 自身为正方形（值为 1），以它为右下角的正方形存在（length>=1).
     *     如果后续两个条件成立， 那么以当前所计算的 element 为右下角的正方形的长度比 相邻对角 element 为右下角的正方形最大长度 多1， 就是成功从对角 element 往外扩了一个长度。
     * 3， 如果 2 条件成立， 假设条件 2 中若对角 element 为右下角的最大正方形 长度为 N 成立， 那么当前所计算的 element 所在的行 也必须满足往左有 N+1 个 element 值为 1（包含自身），
     *      这样以当前计算 element 为右下角的正方形才能成功扩展，否则只能看 当前所计算 element 的左一 element 的正方形情况
     * 4， 和 3 一样， 当前计算 element 的上方 element 也和它的左一一样限制当前 element 为右下角的正方形的最大大小。
     * 
     * 最终公式为：  最小值（matrix[i][j-1], matrix[i-1][j-1], matrix[i-1][j]) +1 
     *     
     * @param matrix
     * @return
     */
    private int msOptimal(int[][] matrix){
        int result = 0;
        int len = matrix.length;
        int colLen = matrix[0].length;

        for(int i=0; i< len; i++){
            for(int j=0; j < colLen; j++){

                if(matrix[i][j] == 1){
                    if(1>result)result=1;
                    if(i==0 || j==0){
                        continue;
                    }else{
                        int minBtLeftAndUp = Math.min(matrix[i][j-1], matrix[i-1][j]);
                        int finalMin = Math.min(minBtLeftAndUp, matrix[i-1][j-1]);
                        matrix[i][j] = 1 + finalMin;
                        if(matrix[i][j] > result)result = matrix[i][j];
                    }
                }
            }
        }
    
        return result*result;

    }


}

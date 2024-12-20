package algoPractice.DP;

public class UniquePathsCnt {

    public static void main(String[] args) {
        UniquePathsCnt ins = new UniquePathsCnt();
        int[][] rr = {{0,0,0}, {0,1,0}, {0,0,0}};
        ins.uniquePathsWithObstacles(rr);

    }

    public int uniquePathsWithObstacles(int[][] obstacleGrid) {

        int[][] path = new int[obstacleGrid.length][obstacleGrid[obstacleGrid.length -1].length];

        for(int i=0; i< obstacleGrid.length; i++){
            int[] row = obstacleGrid[i];
            for(int j=0; j < row.length ; j++){
                if(i==0 && j==0 && obstacleGrid[i][j] ==1) return 0;
                else if(i==0 && j==0 && obstacleGrid[i][j] ==0) path[0][0] = 1;
                else {
                    if(obstacleGrid[i][j] == 1)path[i][j]=0;
                    else {
                        // check left
                         int left = j -1 >= 0 ? path[i][j-1] : 0;
                         // check up
                         int up = i-1 >= 0 ? path[i-1][j] : 0;

                         path[i][j] = left + up;

                    }
                }
            }
        }
        return path[path.length-1][path[path.length-1].length -1];
    }
}

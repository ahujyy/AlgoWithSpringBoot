package algoPractice.DP;

public class MinPathSum {
    public int minPathSum(int[][] grid) {
        for(int i=0; i< grid.length; i++){
            int[] row = grid[i];
            for(int j=0; j< row.length; j++){

                if(j -1 >=0){ // left exit
                    if(i -1 >= 0){// up exit
                        grid[i][j] = Math.min(grid[i][j-1], grid[i-1][j]) + grid[i][j];
                    }else { // only left
                        grid[i][j] = grid[i][j-1] + grid[i][j];
                    }
                } else {
                    if(i -1 >= 0){
                        grid[i][j] = grid[i-1][j] + grid[i][j];
                    }
                }
            }
        }
        return grid[grid.length -1][grid[grid.length-1].length -1];
    }
}

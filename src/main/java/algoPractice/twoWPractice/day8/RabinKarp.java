package algoPractice.twoWPractice.day8;

import java.util.ArrayList;

public class RabinKarp {

    //base 和 mod 互质的（最大公约数为 1）
    private static int base =  31;
    private static int mod = 1000000007;



    public ArrayList<Integer> match(String mainString, String modelString){

        if(null == mainString || null == modelString || mainString.length()==0 || modelString.length()==0)return new ArrayList<>();
        int len = mainString.length();
        int mdLen = modelString.length();

        ArrayList<Integer> startIndex = new ArrayList<>();

        long mdHash =  0;
        long windowHash = 0;
        for(int i=0; i<mdLen; i++){
            mdHash = (mdHash * base + modelString.charAt(i)) % mod; 
            windowHash = (windowHash * base + mainString.charAt(i)) % mod; 
        }

        long powerToRemove = 1;
        for(int i=1; i< mdLen; i++){
            powerToRemove = (powerToRemove* base) % mod;
        }

        
        for(int r =0; r <= len - mdLen; r++){
            
            if(mdHash == windowHash){
                if(checkEqual( mainString.substring(r, r+mdLen),  modelString)){
                    startIndex.add(r);
                }
            }

            if(r < len - mdLen){
                windowHash = (windowHash -  mainString.charAt(r) * powerToRemove ) % mod;
                if(windowHash < 0){
                    windowHash =  windowHash + mod;
                }

                windowHash = ((windowHash * base)  + mainString.charAt(r+mdLen)) %mod;
            
            }

        }


    
        return startIndex;
    }

    private boolean checkEqual(String a , String b){
        for (int j = 0; j < b.length(); j++) {
            if(a.charAt(j) != b.charAt(j))return false;
        }

        return true;
    }


    public static void main(String[] args) {
        RabinKarp rKarp = new RabinKarp();
        String mainStr = "ABABA";
        String modelStr = "ABA";
        System.out.println("匹配到的起始下标: " + rKarp.match(mainStr, modelStr));    

        String mainStrO = "匹配到的起始下标配到其实";
        String modelStrO = "配到";
        System.out.println("匹配到的起始下标: " + rKarp.match(mainStrO, modelStrO));    
    }


}

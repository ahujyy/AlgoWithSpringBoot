package algoPractice;


import java.util.*;

/**
 * We are given two strings P and Q, each consisting of N lowercase English letters. For each position in the strings, we have to choose one letter from either P or Q, in order to construct a new string S, such that the number of distinct letters in S is minimal. Our task is to find the number of distinct letters in the resulting string S.
 *
 * For example, if P = "ca" and Q = "ab", S can be equal to: "ca", "cb", "aa" or "ab". String "aa" has only one distinct letter ('a'), so the answer is 1 (which is minimal among those strings).
 *
 * Write a function:
 *
 * class Solution { public int solution(String P, String Q); }
 *
 * that, given two strings P and Q, both of length N, returns the minimum number of distinct letters of a string S, that can be constructed from P and Q as described above.
 *
 * Examples:
 *
 * 1. Given P = "abc", Q = "bcd", your function should return 2. All possible strings S that can be constructed are: "abc", "abd", "acc", "acd", "bbc", "bbd", "bcc", "bcd". The minimum number of distinct letters is 2, which be obtained by constructing the following strings: "acc", "bbc", "bbd", "bcc".
 *
 * 2. Given P = "axxz", Q = "yzwy", your function should return 2. String S must consist of at least two distinct letters in this case. We can construct S = "yxxy", where the number of distinct letters is equal to 2, and this is the only optimal solution.
 *
 * 3. Given P = "bacad", Q = "abada", your function should return 1. We can choose the letter 'a' in each position, so S can be equal to "aaaaa".
 *
 * 4. Given P = "amz", Q = "amz", your function should return 3. The input strings are identical, so the only possible S that can be constructed is "amz", and its number of distinct letters is 3.
 *
 * Write an efficient algorithm for the following assumptions:
 *
 * N is an integer within the range [1..200,000];
 * strings P and Q are both of length N;
 * strings P and Q are made only of lowercase letters (a−z);
 * strings P and Q contain a total of at most 20 distinct letters.
 */

public class PQString {

    private int chosenCntShared = 0;

    public static void main(String[] args){
        PQString ins = new PQString();
        // System.out.println(ins.solution("loops", "loops"));
        // System.out.println(ins.solution("loops", "labps"));
        System.out.println(ins.solution("lcdps", "labps"));
    }


    public int solution(String P, String Q) {
       if(P == null || Q == null || P.length() != Q.length() )
           throw new RuntimeException("P Q should not be null and have the same length");

       int length = P.length();
       chosenCntShared = length;
       char[] pArr = P.toLowerCase().toCharArray();
       char[] qArr = Q.toLowerCase().toCharArray();

       int charExistInPQBit = 0;
       boolean[][] edgeToCover = new boolean[26][26];
       for(int i=0; i < length; i++){
           int pCharIndex = pArr[i] - 'a';
           int qCharIndex = qArr[i] - 'a';
           edgeToCover[pCharIndex][qCharIndex] = true;
           edgeToCover[qCharIndex][pCharIndex] = true;
           charExistInPQBit |= 1 << pCharIndex;
           charExistInPQBit |= 1 << qCharIndex;
       }

       showString(charExistInPQBit);

       int charChosen = 0;
       int i = 0;
       while (i < 26){
           if(edgeToCover[i][i]){
               // if position exits where chars in P and Q are the same character,
               // then this character must be chosen, then no need the check the
               // edge between it and other characters having pair with it.
               charChosen++;
               for (int x=0; x < 26; x++){
                   edgeToCover[i][x] = false;
                   edgeToCover[x][i] = false;
               }
               charExistInPQBit &= ~( 1 << i);
           }
           i++;
       }
       showString(charExistInPQBit);

       decision4Char(charExistInPQBit, 0, edgeToCover, charChosen);

       return chosenCntShared;
    }

    private void decision4Char(int charExistBit, int charIndex, boolean[][] edgeToCover, int chosenCnt){

        if(charIndex >=26 ){
            if(chosenCnt < chosenCntShared){
                chosenCntShared = chosenCnt;
            }
            return;
        }

        if(chosenCnt >= chosenCntShared)return;


        if( !charExist(charExistBit, charIndex)){
            decision4Char(charExistBit, charIndex +1, edgeToCover, chosenCnt);
        }

        // if checking character is not chosen,  all others characters paring with it must be chosen, then all these
        // other characters could be deleted from graph and the edges of them could not be checked.
        int pairWithIt = 0;
        int notChosenTmpBit = 0;
        for(int x= charIndex + 1; x<26; x++){
            if(edgeToCover[x][charIndex] && edgeToCover[charIndex][x] && charExist(charExistBit, x)){
                pairWithIt ++;
                notChosenTmpBit |=  (1<<x);
                //no need disable edgeToCover for index x because checking charExistBit as well.
            }
        }
        decision4Char(notChosenTmpBit ^ charExistBit  , charIndex +1, edgeToCover, chosenCnt + pairWithIt);

        // if checking character is chosen, chosenCnt need increase by 1, all edges link to this character have been
        // checked, but it doesn't mean pairing characters could be ignored, because others may
        // establish pair with another ones.
        decision4Char(charExistBit, charIndex +1, edgeToCover, chosenCnt +1);

    }

    private boolean charExist(int charExistBit, int index){
        return (charExistBit & (1 << index)) > 0;
    }


    private void showString(int charExistInPQBit){

        StringBuilder stringBuilder = new StringBuilder();
        for(int x =0 ; x < 26 ; x++){
            if( (charExistInPQBit & (1 << x) ) > 0){
                char charNow = (char) ('a' + x);
                stringBuilder.append(charNow + ",");
            }
        }
        if(stringBuilder.length() > 0 ){
            System.out.println("Characters in P or Q : \n" +
                            stringBuilder.substring(0, stringBuilder.length() -1 )
                    );
        }else {
            System.out.println("All characters in P Q are the same.");
        }
    }
}

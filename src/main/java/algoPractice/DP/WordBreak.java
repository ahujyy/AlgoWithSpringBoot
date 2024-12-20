package algoPractice.DP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Given a string s and a dictionary of strings wordDict,
 * return true if s can be segmented into a space-separated sequence of one or more dictionary words.
 *
 * Note that the same word in the dictionary may be reused multiple times in the segmentation.
 *
 * 1 <= s.length <= 300
 * 1 <= wordDict.length <= 1000
 * 1 <= wordDict[i].length <= 20
 * s and wordDict[i] consist of only lowercase English letters.
 * All the strings of wordDict are unique.
 */
public class WordBreak {

    public static void main(String[] args) {
        WordBreak wordBreak = new WordBreak();

        System.out.println(wordBreak.wordBreak("applepenapple", List.of("apple", "pen")));
    }

    public boolean wordBreak(String s, List<String> wordDict) {

       // return nextIdxList(List.of(0), s, wordDict);

        HashSet set = new HashSet(wordDict);
        int len = s.length();
        boolean[] breakMark = new boolean[len +1];
        breakMark[0] = true;
        for(int i=1 ; i <= len; i ++){
            for(int j=0 ; j<i; j++ ){
                if(breakMark[j] && set.contains(s.substring(j, i)) ){
                    breakMark[i] = true;
                    break;
                }
            }
        }
        return breakMark[len];

    }


    /**
     * time out case:
     * s =
     * "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab"
     * wordDict =
     * ["a","aa","aaa","aaaa","aaaaa","aaaaaa","aaaaaaa","aaaaaaaa","aaaaaaaaa","aaaaaaaaaa"]
     *
     * actually no need check all possible word break solutions for one position.
     * @param idxList
     * @param s
     * @param wordDict
     * @return
     */
    private boolean nextIdxList(List<Integer> idxList,  String s, List<String> wordDict){
        boolean result = false;

        for(int idx : idxList){
            List<Integer> matchIdx = new ArrayList<>(wordDict.size());
            for(String word: wordDict ){
                if(idx + word.length()  > s.length())continue;

                if ( s.substring(idx, idx + word.length()).equals(word) ) {
                    int nextCheckPos = idx  + word.length();
                    matchIdx.add(nextCheckPos);
                    if(nextCheckPos == s.length()) {
                        return true;
                    }
                }
            }
            result = result | nextIdxList(matchIdx, s, wordDict);
        }

        return result;
    }
}

package algoPractice.twoWPractice.day8;

import java.util.ArrayDeque;


/**
 * 1，用双指针定位 word 然后用 substring 搞定
 * 2， 我用 ArrayDeque 边界条件比较复杂，多一个 deque 空间复杂度。
 * https://leetcode.com/problems/reverse-words-in-a-string/submissions/2065940338/
 * LC151
 */
public class LC151 {


    public String reverseWords_standard(String s) {
        s = s.trim(); // 去除首尾空格
        int right = s.length() - 1;
        int left = right;
        StringBuilder sb = new StringBuilder();
    
        while (left >= 0) {
            // 1. 从后往前找第一个空格，left 滑动，直到滑到单词的头部
            while (left >= 0 && s.charAt(left) != ' ') left--;
            
            // 2. 此时 [left + 1, right + 1] 就是一个完整的单词，直接截取
            sb.append(s.substring(left + 1, right + 1)).append(" ");
            
            // 3. 继续往前跳过所有连续的中间空格
            while (left >= 0 && s.charAt(left) == ' ') left--;
            
            // 4. 让 right 追上 left，准备锁定制动下一个单词
            right = left;
        }
        
        return sb.toString().trim(); // 去掉最后多出来的一个空格
    }


    public String reverseWordsSeparte(String s){
        String nospaceHeadTail = s.trim();
        String[]  list = nospaceHeadTail.split(" ");

        StringBuilder sb = new StringBuilder();
        for(int i = list.length -1; i>=0; i--){
            String item = list[i];
            if(!item.equals("")){
                sb.append(item);
                if(i != 0){
                    sb.append(" ");
                }
            }
        }
        return sb.toString();

    }

    public String reverseWords(String s) {
        int length = s.length();
        ArrayDeque<Character> stack = new ArrayDeque<Character>();
        StringBuilder sb = new StringBuilder();
        for (int i=length-1; i>=0; i--){
            char ch = s.charAt(i);
            if(ch == ' ' &&  !stack.isEmpty()  && !stack.peek().equals(' ') ){
                stack.push(ch);
            }else if(ch != ' ' && (stack.isEmpty() || !stack.peek().equals(' '))){
                stack.push(ch);
            }else if(ch != ' ' && stack.peek().equals(' ')){
                stack.pop();
                while (!stack.isEmpty()){
                    Character wordChar = stack.pop();
                    sb.append(wordChar);
                }
                sb.append(" ");
                stack.push(ch);
            }
        }

        while(!stack.isEmpty()){
            char wordChar = stack.pop();
            if(wordChar != ' ')sb.append(wordChar);
        }
        return sb.toString();
    }

}

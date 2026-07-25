package algoPractice.twoWPractice.day2;

import java.util.HashSet;

/**
 * InnerLC3  https://leetcode.com/problems/longest-substring-without-repeating-characters/description/
 * 
 * 严格子串不含重复字符，前面一个位置开始L，到后面一个位置R， 长度是 R-L+1， R+1位置元素和 L~R当中一个元素重复。 
 * 这样L 位置开始就结束，就是它的最大长度，接着看L+1 位置，要找到L~R当中与R+1元素重复元素的位置，L 要移动到此位置的下一个位置，因为以L 到重复元素位置
 * 的元素作为开头的子串都比 L 到 R 短。然后接着计算新L位置开始，到 R+1后续子串的长度，这里唯一可以重复利用的是前面 L+1 到 R 之间的元素已知，
 * 放在 set 里能快速判断新元素是否重复，也就是说 L 位置前移一位，要把原 L
 * 位置元素从 Set 中移除。添加新元素让 R 往前移，看是否能突破最大子串不含重复元素。
 * 
 */
public class LC3 {
    public int lengthOfLongestSubstring(String s) {
        
        int max =  0, left=0, right =0;
        int len = s.length();

        HashSet<Character> charSet = new HashSet<Character>();
        while(right < len){
            Character joinChar = s.charAt(right);
            if(charSet.contains(joinChar)){
                // L～R 已经有了joinChar，去找它的位置，把此位置前面都删了，因为 L 到此位置都子串不会比 R-L+1 长。 
                int subStringLen = right - left;
                if(subStringLen > max)max=subStringLen;

                while(left < right){
                    charSet.remove(s.charAt(left));
                    Character currentChar = s.charAt(left);
                    left++;
                    if(currentChar.equals(joinChar)){//找到和joinChar 相等的位置 left, left 已经提前+1 到下一位了
                        break;
                    }
                }

                charSet.add(joinChar);
                right++;

            }else{
                charSet.add(joinChar);
                right++;
            }
        }
        
         //最后一个子串的检查   
        if((len-left)>max ) max= len-left;
        
        
        // if(left==0 && right==0)return 0; //""
        // if(left==0 && right==len) return len; // 子串和 string 是同一个，没有任何重复

        return max;
    }
}

package algoPractice.twoWPractice.day3;

import java.util.Stack;

public class LC20 {

    public boolean isValid(String s) {
        boolean valid = false;
        Stack<Character> stack = new Stack<Character>();
        Character a = '(', b='[', c='{',  d=')', e=']', f='}';


        for(int i=0; i<s.length(); i++){
            Character character = s.charAt(i);
            if(character.equals(a) || character.equals(b) || character.equals(c) ){
                stack.push(character);
            }else if(character.equals(d) && !stack.isEmpty()) { // pop throws exception when stack is empty 
                Character pp = stack.pop();
                if(!pp.equals(a))return false;
            }else if(character.equals(e) && !stack.isEmpty()) {
                if(!stack.pop().equals(b))return false;
           
            }else if(character.equals(f) && !stack.isEmpty()) {
                if(!stack.pop().equals(c))return false;
            }else{
               return false;
            }
        }
        valid = stack.isEmpty()? true:false;
        return valid;
        
    }

    public static void main(String[] args) {
        LC20 lc20 = new LC20();
        System.out.print(lc20.isValid("(]"));
    }
}

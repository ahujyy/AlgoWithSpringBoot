package algoPractice.twoWPractice.day3;

import java.util.Stack;

/**
 * https://leetcode.com/problems/implement-queue-using-stacks/   
 * 用两个 stack 来实现 FIFO queue ， stack 正好相反是先进后出。 利用一个做备份，添加新元素时把老的转到备份，完成添加新元素，再把备份的挪回来。
 * LC232
 */
public class LC232 {
    private Stack<Integer> mainStack ;
    private Stack<Integer> backupStack ;
    public LC232() {
        mainStack = new Stack<Integer>();
        backupStack = new Stack<Integer>();
    }
    
    public void push(int x) {
        if(mainStack.isEmpty()){
            mainStack.push(x);
            return;
        }
        while(!mainStack.isEmpty()){
            backupStack.push( mainStack.pop());
        }
        mainStack.push(x);
                
        while(!backupStack.isEmpty()){
            mainStack.push( backupStack.pop());
        }
        
    }
    
    public int pop() {
       return mainStack.pop();
    }
    
    public int peek() {
      return  mainStack.peek();
    }
    
    public boolean empty() {
        return mainStack.isEmpty();
    }
}

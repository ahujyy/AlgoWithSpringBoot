package algoPractice.twoWPractice.day3;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * https://leetcode.com/problems/min-stack/
 * Design a stack that supports push, pop, top, and retrieving the minimum element in constant time.
 * 要求： O(1) time complexity for each function.
 * 
 * 这里 stack 用 ArrayDeque 代替， 性能更好，stack 是线程安全的有开销。
 * Stack 是 LIFO，主体思路是，每当主栈压栈一个元素，辅助栈就添加一个压栈后主栈当前最小的元素， 也就是说如果当前元素小于等于当前栈最小元素时，辅助栈就压入该元素。
 * 而当大于当前主栈最小元素时， 该元素入栈后主栈的最小元素仍然和原来一样，也就是说辅助栈压入一个和当前 top 元素一模一样的元素，但是可以这样优化：当前元素比最小元素后进主栈，
 * 它在不过改变最小元素，它出栈时重要的点是最小元素肯定没有出栈，所以轮不到它做最小元素，所以它不用入辅助栈。
 * 反过来如果当前元素比较小，它肯定要入辅助栈，当该元素出栈时，它前面入栈的大元素还在，这些元素的最小元素在它之前入了辅助栈，所以只要它同时出辅助栈就可以了。
 * 
 * 
 * LC155
 */
public class LC155 {
    private Deque<Integer> stack;
    private Deque<Integer> minValStack;

    public LC155() {
        stack = new ArrayDeque<Integer>();
        minValStack = new ArrayDeque<Integer>();
        
    }
    
    public void push(int value) {
        if( minValStack.isEmpty() 
            || minValStack.peek() >= value
        ) minValStack.push(value);

        stack.push(value);
    }
    
    public void pop() {
        int pv = stack.pop();
        if(pv == minValStack.peek()){
           minValStack.pop();
        }
        
    }
    
    public int top() {
        return stack.peek();
    }
    
    public int getMin() {
       return minValStack.peek(); 
    }
}

/**
 * 知识点：
 * 1，Stack 是继承 Vector，有 synchronized 在保证线程安全，所以性能不好
 * ArrayDeque 即实现了 Deque (offer, poll, peek), 同时它自己也提供栈的方法 pop , push, peek. 它性能更好，官方推荐ArrayDeque 代替 stack
 * 
 * 2，名称概念总是混淆点： Deque 中的 De 是 double-ended的意思， 翻译中文是 双端队列。  
 * 而 双向链表 指 linkedlist， ArrayDeque 中的 Array 顾名思义，和 Arraylist 一样，是数组实现， 然后加两个 head ，tail 指针就可以，很轻量。
 * 这种 array 是连续内存，cpu 缓存因为块读取容易命中，性能比linkedlist链表这种分散的 node 用 双向指针连起来的结构更好。
 * 
 * 3，大部分情况是 arraylist 和 linkedlist 分开用，一个用数组连续性，index 随机访问， 另外一个是用双向指针快速添加和删除。各有千秋。
 * 同时 linkedhashmap 利用的是后面一个思想多一点，因为虽然存储是 array，但是元素用 hash 取模打乱了， 不连续，但是它 node 有双向指针，典型用法 LRU ，双向很容易添加删除元素。
 * 
 * 4，同时结合 array 和 link 指针是叫 “数组化链表（Array-backed Linked List）”或“对象池（Object Pooling）”
 * 
 * LRU 缓存（Least Recently Used）：手写高性能缓存时，我们需要在 O(1) 时间内把任意一个节点移到链表头部。
 * 如果用 HashMap<Integer, Node>，Node 是分散在堆里的对象，GC 压力大。高性能写法会用数组 + 两个 int 指针（prevIndex 和 nextIndex）来模拟链表。 
 * ------
 * 
 * 游戏引擎中的实体组件（Entity Component System）：需要快速遍历大量存活的实体，并频繁创建/销毁中间实体，为了不产生内存碎片，会使用数组池 + 空闲链表（Free List）。
 * 
 * 内存池 / 连接池：为了重用对象，避免频繁 new，会维护一个数组，并用链表指针串起所有“空闲块”。
 */


/**

对于那 1% 的极致场景（“必须有”的时刻）：

高频交易系统：每微秒都至关重要，不允许 GC 停顿。

游戏引擎（服务端）：每秒处理数万玩家移动，创建/销毁海量实体，GC 会导致掉帧。

嵌入式系统 / Android 底层：内存极其有限，无法承受每个节点 48 字节的对象头开销。

在这些场景下，对象引用就是洪水猛兽，必须用 int 下标替代。因为 int 只有 4 字节，而且数组是基本类型，根本没有对象头，GC 根本不会扫描这片内存区域。

public class ArrayBackedLRU<K, V> {
    // 1. 哈希表：负责通过 key 快速找到数据在数组中的位置（下标）
    //    key 是业务键，value 是数据在数组中的索引（int）
    private Map<K, Integer> keyToIndexMap = new HashMap<>(); 
    
    // 2. 数组池（连续内存），用来存放真实数据
    private Object[] keys;    // 或者用 K[] keys
    private V[] values;
    private int[] prev;
    private int[] next;
    
    private int head, tail, cursor, capacity;


    public ArrayBackedLRU(int capacity) {
        keys = new int[capacity];
        values = new int[capacity];
        prev = new int[capacity];
        next = new int[capacity];
        // 初始化：-1 代表 null（无指向）
        head = -1; 
        tail = -1;
    }
    
    // 将下标为 nodeIndex 的节点移动到链表头部
    private void moveToHead(int nodeIndex) {
        // 1. 从原位置摘除
        int p = prev[nodeIndex];
        int n = next[nodeIndex];
        if (p != -1) next[p] = n;
        else head = n; // 如果是头节点，更新 head
        
        if (n != -1) prev[n] = p;
        else tail = p; // 如果是尾节点，更新 tail
        
        // 2. 插入到头部
        prev[nodeIndex] = -1;
        next[nodeIndex] = head;
        if (head != -1) prev[head] = nodeIndex;
        head = nodeIndex;
        if (tail == -1) tail = nodeIndex;
    }
    
    // 获取数据
    public V get(K key) {
        // 通过哈希表 O(1) 定位到数组下标
        Integer index = keyToIndexMap.get(key);
        if (index == null) return null;
        
        // 把这个下标移到链表头部（LRU 更新）
        moveToHead(index);
        return values[index];
    }
    
    // 放入数据
    public void put(K key, V value) {
        if (keyToIndexMap.containsKey(key)) {
            int idx = keyToIndexMap.get(key);
            values[idx] = value;
            moveToHead(idx);
            return;
        }
        
        // 如果满了，淘汰尾节点
        if (cursor >= capacity) {
            int tailIndex = tail;
            // 从哈希表中移除被淘汰的 key
            keyToIndexMap.remove(keys[tailIndex]);
            // 覆盖掉尾节点的位置（复用数组槽位）
            // 注意：需要把新 key 放进去
            keys[tailIndex] = key;
            values[tailIndex] = value;
            // 把这个被覆盖的节点移动到头部
            moveToHead(tailIndex);
            // 更新哈希表
            keyToIndexMap.put(key, tailIndex);
        } else {
            // 有空闲槽位
            int newIdx = cursor++;
            keys[newIdx] = key;
            values[newIdx] = value;
            prev[newIdx] = -1;
            next[newIdx] = -1;
            // 插入链表头部...
            // 更新哈希表
            keyToIndexMap.put(key, newIdx);
        }
    }
}


 */
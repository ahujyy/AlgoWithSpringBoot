package algoPractice.twoWPractice.day6;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * https://leetcode.com/problems/lru-cache/
 * LC146
 * 
 * 要求是 【LeetCode 146. LRU 缓存（必须能手写单链表 + 哈希表的实现】 
 * 那这里的单链表不是双链表， 访问后从链表中移到尾部的时候，从当前节点找不到前面节点，只能找到后面节点。
 * 解决方案一是 既然找不到当前节点的前节点，那么直接访问前节点，这个节点是能访问到真正节点的，后面的也能接着访问到。
 * 
 * 
 * 用单链表代替双链表时，map 中key 存的（对应的）是当前 node 的前一个 node， 这样map 拥有多少个元素，还是拥有多少个 key，
 * 只是key 对应的 node 是链表中它的前一个，所以第一元素得找一个 node，该 node 的 next 是该第一个元素 node，这样就要
 * 引入一个 head 的 dummy node。  而尾部追加元素时，只要把 tail 指向最右一个有意义的元素 node 就可以了。
 */
public class LC146 {

    private LinkedHashMap<Integer, Integer> hashMap;

    public LC146(){

    }

    public LC146(int capacity) {
        hashMap = new LinkedHashMap<Integer, Integer>(2* capacity, 0.75f, true){
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<Integer, Integer> eldest) {
               // 不能等于这里， 因为是 put 后被调用的，当刚刚插入一个数后size 刚好是capacity 而删除一个不是期望的。 
                if(this.size() > capacity) return true;
                return super.removeEldestEntry(eldest);
            }
        };
    }
    
    public int get(int key) {
        
        Integer v = hashMap.get(key);
        if(v == null)return -1;
        else return v;
        
    }
    
    public void put(int key, int value) {
        hashMap.put(key, value);
    }

//------------------------------------
    static class Node<K, V>{
        private V value;
        private Node< K, V> next;
        private K key;


    }

    private HashMap<Integer, Node<Integer, Integer>> map;
    private int capacity;
    private Node<Integer, Integer> head;
    private Node<Integer, Integer> tail;

    public LC146 instance(int capacity){
        LC146 lc146 = new LC146();
        map = new HashMap<Integer, Node<Integer, Integer>>();
        this.capacity = capacity;
        head = new Node<Integer, Integer>();
        return lc146;
    }

    public int getK(int key) {
        Node<Integer, Integer> node = map.get(key);
        if(node == null) return -1;
        
        Node<Integer, Integer> target = node.next; 
        Integer val = target.value;

        if(tail == target) return val;
        // 添加到后面去  key <---> node1,  node1-->node2-->node3  ===>  key <--->tail, node1--->node3,  key_node3<--->node1, tail --->node2
        node.next = node.next.next;
        map.put(node.next.key, node);
        
        map.put(key, tail);
        tail.next = target;
        tail = target;

        return val;
        
    }

    public void putV(int key, int value) {
        
        Node<Integer, Integer> target  = new Node<>();
        target.key = key;
        target.value = value;

        if(tail != null){ // 链上有值
            boolean update= map.containsKey(key);
            if(update){
                get(key);
                tail.value = value;
                
            }else{
                map.put(key, tail);
                tail.next = target;
                tail = target;
            }


        }else{ // 尾部为空，添加在 head 后面
            map.put(key, head);
            head.next = target;
            tail = target;
        }

        if(map.size() > capacity){
            Node<Integer, Integer> eldest = head.next;
            Node<Integer, Integer> nextNode = eldest.next;

            map.remove(eldest.key);

            if(nextNode != null){
                map.put(nextNode.key, head);//更新 key
                head.next = nextNode;
            }

        }

    }

}

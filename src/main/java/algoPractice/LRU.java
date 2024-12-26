package algoPractice;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Least recently used algo
 */
public class LRU<K, V> {

    private int capacity;
    private boolean linkedHashMap;

    private Map<K,V>  map;

    private Map<K, Node<K,V>> selfMap;


    /**
     * dummy node's next node is the head,  dummy node's previous node is the end.
     */
    private Node dummyNode;



    static class Node<K,V> {
        K key;
        V value;
        Node<K,V> previousNode;

        Node<K,V> nextNode;

        Node(K key, V value){
            this.key = key;
            this.value = value;

            previousNode = this;
            nextNode = this;
        }

    }
    public LRU(int capacity, boolean linkedHashMap ){
        this.capacity = capacity;
        this.linkedHashMap = linkedHashMap;

        if(linkedHashMap){
            map = new LinkedHashMap<K, V>(capacity* 2,0.75f, true  ){

                @Override
                protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                    return this.size() > capacity;
                }
            };
        }else {
            dummyNode = new Node(null, null);

            selfMap = new HashMap<>();
        }
    }

    public V get(K key){
        if(linkedHashMap){
            return map.get(key);
        }else {

            Node<K,V> node = selfMap.get(key);
            if(node == null)return null;

            //update
            node.previousNode.nextNode = node.nextNode;
            node.nextNode.previousNode = node.previousNode;
            //selfMap.remove(key);

            //insert into the head

            node.nextNode = dummyNode.nextNode;
            node.previousNode = dummyNode;

            dummyNode.nextNode.previousNode = node;
            dummyNode.nextNode = node;
            //selfMap.put(key, node);

            return node.value;
        }
    }

    public void put(K key, V value){

        if(linkedHashMap){
            map.put(key, value);
            return;
        }

        Node<K,V> node = selfMap.get(key);
        if(node == null){
            Node<K, V> newOne = new Node<>(key, value);
            selfMap.put(key, newOne);
            newOne.nextNode = dummyNode.nextNode;
            newOne.previousNode = dummyNode;

            dummyNode.nextNode.previousNode = newOne;
            dummyNode.nextNode = newOne;

            if(selfMap.size() > capacity){
                //remove from the end
                Node<K, V> lastOne = dummyNode.previousNode;
                selfMap.remove(lastOne.key);
                lastOne.previousNode.nextNode = dummyNode;
                dummyNode.previousNode = lastOne.previousNode;
            }
        } else {
            //remove node
            node.previousNode.nextNode = node.nextNode;
            node.nextNode.previousNode = node.previousNode;

            Node<K, V> newOne = new Node<>(key, value);
            selfMap.put(key, newOne);

            dummyNode.nextNode.previousNode = newOne;
            newOne.nextNode = dummyNode.nextNode;
            newOne.previousNode = dummyNode;
            dummyNode.nextNode = newOne;
        }

    }
}

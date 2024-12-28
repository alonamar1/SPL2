package bgu.spl.mics.application.objects;

public class ReadyToProcessPair<K, V> {
    private K key;
    private V value;

    public ReadyToProcessPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}

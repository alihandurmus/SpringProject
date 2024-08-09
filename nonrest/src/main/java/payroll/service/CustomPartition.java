package payroll.service;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class CustomPartition implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        String keyString = (String) key;
        int partition = 0;
        switch (keyString) {// Keylere göre özel partition id ayarlama
            case "GET":
                partition = 0;
                break;
            case "POST":
                partition = 1;
                break;
            case "PUT":
                partition = 2;
                break;
            case "DELETE":
                partition = 3;
                break;
        }
        return partition;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}

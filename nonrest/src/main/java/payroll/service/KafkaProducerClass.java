package payroll.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import payroll.model.HttpRequestStatistic;

@Service
public class KafkaProducerClass {//kafka partitionlara bak.
    private final String topicName = "request_stats";


    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String message) {
        HttpRequestStatistic requestStatistic = new HttpRequestStatistic(message);
        String key = requestStatistic.getRequest_type(); // Key olarak request_type belirlendi
        kafkaTemplate.send(topicName, key, message);
        System.out.println("Sent record(key=" + key + " value=" + message + ")");
    }
}
/*package payroll.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import payroll.model.HttpRequestStatistic;
import payroll.repository.HttpRequestStatisticRepository;

@Service
public class KafkaConsumer {
    @Autowired
    private HttpRequestStatisticRepository httpRequestStatisticRepository;
    @KafkaListener(topics = "request_stats",groupId = "group_id")
    public void listen(String message) {
        System.out.println(message);
        HttpRequestStatistic httpRequestStatistic = new HttpRequestStatistic(message);
        httpRequestStatisticRepository.save(httpRequestStatistic);
    }
}*/

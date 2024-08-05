package payroll.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import payroll.model.HttpRequestStatistic;
import payroll.repository.HttpRequestStatisticRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class StaticsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HttpRequestStatisticRepository httpRequestStatisticRepository;

    public List<StatisticsResult> getHttpRequestStatistics(Date startDate, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DATE, 1);
        Date justAddEndDate = calendar.getTime();

        MatchOperation dateMatch = Aggregation.match(Criteria.where("timestamp").gte(startDate.getTime()).lte(justAddEndDate.getTime()));


        Aggregation aggregation = Aggregation.newAggregation(
                dateMatch,
                Aggregation.project("request_type", "request_status", "timestamp")
                        .andExpression("dateToString('%Y-%m-%d', { $toDate: '$timestamp' })").as("date"),
                Aggregation.group("date", "request_type", "request_status")
                        .count().as("count"),
                Aggregation.group("_id.date")
                        .push(new Document("method", "$_id.request_type")
                                .append("status", "$_id.request_status")
                                .append("count", "$count")).as("counts"),
                Aggregation.project("counts").and("_id").as("date")
        );

        AggregationResults<StatisticsResult> results = mongoTemplate.aggregate(aggregation, "httpRequestStatistic", StatisticsResult.class);

        // Hata ayıklama için sonuçları konsola yazdırın
        results.getMappedResults().forEach(result -> {
            System.out.println("Date: " + result.getDate());
            result.getCounts().forEach(count -> {
                System.out.println("Method: " + count.getMethod() + ", Status: " + count.getStatus() + ", Count: " + count.getCount());
            });
        });

        return results.getMappedResults();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatisticsResult {
        private String date;
        private List<CountByMethodStatus> counts;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class CountByMethodStatus {
            private String method;
            private String status;
            private int count;
        }
    }
}

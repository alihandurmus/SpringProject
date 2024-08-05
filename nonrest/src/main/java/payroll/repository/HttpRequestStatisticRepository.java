package payroll.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import payroll.model.HttpRequestStatistic;

public interface HttpRequestStatisticRepository extends MongoRepository<HttpRequestStatistic,String> {

}

package payroll.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import payroll.model.HttpRequestStatistic;
import payroll.service.StaticsService;

import java.util.List;

@RestController
@RequestMapping("/api/statistic")
public class StaticsController {
    @Autowired
    private StaticsService staticsService;
    @GetMapping("/daily")
    public List<StaticsService.StatisticsResult> getDailyStatistics() {
        return null; //staticsService.getHttpRequestStatistics();
    }
}

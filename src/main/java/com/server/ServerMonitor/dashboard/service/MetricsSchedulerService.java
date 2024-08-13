package com.server.ServerMonitor.dashboard.service;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.server.ServerMonitor.dashboard.repo.MetricsRepo;


@Service
public class MetricsSchedulerService {

    @Autowired
    private SystemMetricesService metricsService;


    @Autowired
    private MetricsRepo metricsRepository;


    

    @Scheduled(fixedRate = 900000)  // 900000 ms = 15 minutes
    public void saveMetricsDataPeriodically() {
        metricsService.saveMetricsData();
    }



    @Scheduled(fixedRate = 3600000) // 3600000 ms = 1 hour
    public void deleteOldMetricsData() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        metricsRepository.deleteOlderThan(cutoff);
    }

}


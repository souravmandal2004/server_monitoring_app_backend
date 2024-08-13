package com.server.ServerMonitor.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.server.ServerMonitor.dashboard.dto.CpuUsageTimestampDTO;
import com.server.ServerMonitor.dashboard.model.MetricsData;
import com.server.ServerMonitor.dashboard.service.SystemMetricesService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    @Autowired
    private SystemMetricesService metricsService;

    @GetMapping("/memory")
    public Map<String, Object> getMemoryMetrics() {
        Map<String, Object> memoryMetrics = new HashMap<>();
        memoryMetrics.put("Total Memory", metricsService.getTotalMemory());
        memoryMetrics.put("Available Memory", metricsService.getAvailableMemory());
        memoryMetrics.put("memoryUsagePercentage", metricsService.getMemoryUsage());
        return memoryMetrics;
    }

    @GetMapping("/disk")
    public Map<String, Object> getDiskMetrics() {
        Map<String, Object> diskMetrics = new HashMap<>();
        diskMetrics.put("Total Disk Space", metricsService.getTotalDiskSpace());
        diskMetrics.put("Available Disk Space", metricsService.getUsableDiskSpace());
        diskMetrics.put("diskUsagePercentage", metricsService.getDiskUsage());
        return diskMetrics;
    }

    @GetMapping("/processes")
    public Map<String, Object> getRunningProcesses() {
        Map<String, Object> processMetrics = new HashMap<>();
        processMetrics.put("Running Process Count", metricsService.getRunningProcessesCount());
        return processMetrics;
    }

    @GetMapping("/process-names")
    public List<String> getProcessNames () {
        return metricsService.getRunningProcesses ();
    } 


    @GetMapping("/latest")
    public MetricsData getLatestMetricsData() {
        return metricsService.getLatestMetricsData();
    }

    @GetMapping ("/cpu/usage")
    public int getCpuUsage () {
        return metricsService.getCpuUsage();
    }


    @GetMapping("/cpu/usage/per-process")
    public Map<String, Double> getCpuUsagePerProcess() {
        return metricsService.getCpuUsagePerProcess();
    }

    @GetMapping("/latest-cpu-usage")
    public List<CpuUsageTimestampDTO> getLatestCpuUsage() {
        return metricsService.getLatestCpuUsage();
    }
}

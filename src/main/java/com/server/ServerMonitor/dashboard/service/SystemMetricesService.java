package com.server.ServerMonitor.dashboard.service;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OSProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.server.ServerMonitor.dashboard.dto.CpuUsageTimestampDTO;
import com.server.ServerMonitor.dashboard.model.MetricsData;
import com.server.ServerMonitor.dashboard.repo.MetricsRepo;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.BinaryOperator;

@Service
public class SystemMetricesService {

    @Autowired
    private MetricsRepo metricsRepository;

    private final SystemInfo systemInfo;
    private final GlobalMemory memory;
    private final CentralProcessor processor;
    private final OperatingSystem os;

    public SystemMetricesService() {
        this.systemInfo = new SystemInfo();
        this.memory = systemInfo.getHardware().getMemory();
        this.processor = systemInfo.getHardware().getProcessor();
        this.os = systemInfo.getOperatingSystem();
    }

    public long getTotalMemory() {
        return memory.getTotal();
    }

    public long getAvailableMemory() {
        return memory.getAvailable();
    }

    public double getMemoryUsage() {
        long usedMemory = getTotalMemory() - getAvailableMemory();
        return (double) usedMemory / getTotalMemory() * 100;
    }

    public long getTotalDiskSpace() {
        return systemInfo.getOperatingSystem().getFileSystem().getFileStores().stream()
                .mapToLong(fs -> fs.getTotalSpace()).sum();
    }

    public long getUsableDiskSpace() {
        return systemInfo.getOperatingSystem().getFileSystem().getFileStores().stream()
                .mapToLong(fs -> fs.getUsableSpace()).sum();
    }

    public double getDiskUsage() {
        long totalSpace = getTotalDiskSpace();
        long usableSpace = getUsableDiskSpace();
        long usedSpace = totalSpace - usableSpace;
        return (double) usedSpace / totalSpace * 100;
    }

    public List<String> getRunningProcesses() {
        List<OSProcess> processes = os.getProcesses(
                p -> true,  // Predicate to include all processes
                Comparator.comparing(OSProcess::getProcessID), // Comparator to sort by process ID
                0 
        );
        return processes.stream().map(OSProcess::getName).collect(Collectors.toList());
    }
    public int getRunningProcessesCount() { 
        return os.getProcessCount();
    }

    public int getCpuUsage() {
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // Sleep for a short time to get the change in ticks
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long[] ticks = processor.getSystemCpuLoadTicks();
        long totalCpu = 0;
        long idleCpu = 0;
        for (int i = 0; i < ticks.length; i++) {
            totalCpu += ticks[i] - prevTicks[i];
        }
        idleCpu = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        double percentage =  100.0 * (totalCpu - idleCpu) / totalCpu;
        int percentageInInteger = (int) percentage;
        return percentageInInteger;
    }


    // New method to get CPU usage per process
    public Map<String, Double> getCpuUsagePerProcess() {
        List<OSProcess> processes = os.getProcesses(
                p -> true,  // Predicate to include all processes
                Comparator.comparing(OSProcess::getProcessID), // Comparator to sort by process ID
                0
        );
    
        long totalCpuTime = processor.getSystemCpuLoadTicks()[CentralProcessor.TickType.IDLE.getIndex()];
    
        // Merging function to handle duplicate keys
        BinaryOperator<Double> mergeFunction = (oldValue, newValue) -> oldValue + newValue;
    
        Map<String, Double> cpuUsageMap = processes.stream().collect(Collectors.toMap(
                OSProcess::getName,
                p -> (100d * (p.getKernelTime() + p.getUserTime())) / totalCpuTime,
                mergeFunction // Handle duplicate keys by summing their CPU usage
        ));
    
        // Sorting the map by value in descending order
        return cpuUsageMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // In case of a tie, keep the first entry
                        LinkedHashMap::new // Use LinkedHashMap to preserve the order
                ));
    }


    public MetricsData getLatestMetricsData() {
        return metricsRepository.findLatest();
    }

    public List<CpuUsageTimestampDTO> getLatestCpuUsage() {
        return metricsRepository.findLatestCpuUsage(PageRequest.of(0, 20));
    }


    public void saveMetricsData() {
        MetricsData metricsData = new MetricsData();
        metricsData.setTotalMemory(getTotalMemory());
        metricsData.setAvailableMemory(getAvailableMemory());
        metricsData.setTotalDiskSpace(getTotalDiskSpace());
        metricsData.setUsableDiskSpace(getUsableDiskSpace());
        metricsData.setNumberOfProcesses(getRunningProcessesCount());
        metricsData.setCpuUsage(getCpuUsage());
        metricsRepository.save(metricsData);
    }
}

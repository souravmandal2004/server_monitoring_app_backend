package com.server.ServerMonitor.dashboard.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "server_details")
@Data
public class MetricsData {
    @Id
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    private long totalMemory;
    private long availableMemory;

    private long totalDiskSpace;
    private long usableDiskSpace;

    private int numberOfProcesses;

    private int cpuUsage;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
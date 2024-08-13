package com.server.ServerMonitor.dashboard.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
@Data
public class CpuUsageTimestampDTO {
    private int cpuUsage;
    private LocalDateTime timestamp;

    public CpuUsageTimestampDTO(int cpuUsage, LocalDateTime timestamp) {
        this.cpuUsage = cpuUsage;
        this.timestamp = timestamp;
    }

}
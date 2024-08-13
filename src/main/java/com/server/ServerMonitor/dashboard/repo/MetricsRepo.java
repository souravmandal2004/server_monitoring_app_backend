package com.server.ServerMonitor.dashboard.repo;

import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.server.ServerMonitor.dashboard.dto.CpuUsageTimestampDTO;
import com.server.ServerMonitor.dashboard.model.MetricsData;
import jakarta.transaction.Transactional;
import java.util.List;


@Repository
public interface MetricsRepo extends JpaRepository<MetricsData, LocalDateTime> {

    @Transactional
    @Modifying
    @Query("DELETE FROM MetricsData m WHERE m.timestamp < :cutoff")
    void deleteOlderThan(LocalDateTime cutoff);

    @Query("SELECT m FROM MetricsData m ORDER BY m.timestamp DESC LIMIT 1")
    MetricsData findLatest();

    @Query("SELECT new com.server.ServerMonitor.dashboard.dto.CpuUsageTimestampDTO(m.cpuUsage, m.timestamp) " +
            "FROM MetricsData m ORDER BY m.timestamp DESC")
    List<CpuUsageTimestampDTO> findLatestCpuUsage(PageRequest pageRequest);
}
package com.practice.hrbank.repository;

import com.practice.hrbank.entity.ChangeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    @Query("SELECT c FROM ChangeLog c WHERE " +
            "(:employeeNumber IS NULL OR LOWER(c.employeeNumber) = LOWER(:employeeNumber)) " +
            "AND (:type IS NULL OR c.type = :type) " +
            "AND (:memo IS NULL OR c.memo LIKE %:memo%) " +
            "AND (:ipAddress IS NULL OR c.ipAddress = :ipAddress) " +
            "AND (:atFrom IS NULL OR c.at >= :atFrom) " +
            "AND (:atTo IS NULL OR c.at <= :atTo) " +
            "AND (:idAfter IS NULL OR c.id > :idAfter OR :idAfter IS NULL) " +
            "ORDER BY c.at DESC, c.id ASC")
    Page<ChangeLog> findByFilters(
            @Param("employeeNumber") String employeeNumber,
            @Param("type") String type,
            @Param("memo") String memo,
            @Param("ipAddress") String ipAddress,
            @Param("atFrom") String atFrom,
            @Param("atTo") String atTo,
            @Param("idAfter") Long idAfter,
            Pageable pageable
    );

    @Query("SELECT COUNT(c) FROM ChangeLog c WHERE c.at BETWEEN :fromDate AND :toDate")
    Long countByAtBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}

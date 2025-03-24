package com.practice.hrbank.repository;

import com.practice.hrbank.entity.ChangeLog;
import java.time.Instant;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    @Query(
        value = "SELECT * FROM change_logs WHERE " +
            "(:employeeNumber IS NULL OR employee_number = :employeeNumber) " +
            "AND (:type IS NULL OR type = :type) " +
            "AND (:memo IS NULL OR memo LIKE CONCAT('%', :memo, '%')) " +
            "AND (:ipAddress IS NULL OR ip_address = :ipAddress) " +
            "AND at >= COALESCE(:atFrom, TO_TIMESTAMP('1970-01-01', 'YYYY-MM-DD')) " +
            "AND at <= COALESCE(:atTo, NOW()) " +
            "AND id > COALESCE(:idAfter, 0) " +
            "ORDER BY at DESC, id ASC",
        nativeQuery = true
    )
    Page<ChangeLog> findByFilters(
        @Param("employeeNumber") String employeeNumber,
        @Param("type") String type,
        @Param("memo") String memo,
        @Param("ipAddress") String ipAddress,
        @Param("atFrom") Instant atFrom,
        @Param("atTo") Instant atTo,
        @Param("idAfter") Long idAfter,
        Pageable pageable
    );


    @Query("SELECT COUNT(c) FROM ChangeLog c WHERE c.at BETWEEN :fromDate AND :toDate")
    Long countByAtBetween(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
}

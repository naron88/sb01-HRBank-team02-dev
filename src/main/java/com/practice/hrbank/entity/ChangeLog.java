package com.practice.hrbank.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "change_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    Type type;

    @Column(nullable = false, length = 30)
    String employeeNumber;

    @Column(nullable = false, columnDefinition = "TEXT")
    String detail;

    @Column(nullable = false, length = 255)
    String memo;

    @Column(nullable = false, length = 20)
    String ipAddress;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    LocalDateTime at;

    public ChangeLog(Type type, String employeeNumber, String detail, String memo, String ipAddress) {
        this.type = type;
        this.employeeNumber = employeeNumber;
        this.detail = detail;
        this.memo = memo;
        this.ipAddress = ipAddress;
        this.at = LocalDateTime.now();
    }

    public enum Type{
        CREATED, UPDATED, DELETED
    }

}
package com.practice.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "backups")
@Getter
@NoArgsConstructor
public class Backup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String worker;

  @Column
  private Instant startedAt;

  @Column
  private Instant endedAt;

  @Column
  @Enumerated(EnumType.STRING)
  Status status;

  @OneToOne
  @JoinColumn(name = "metadata_id")
  Metadata file;

  public Backup(Metadata file, Status status, Instant startedAt, Instant endedAt, String worker) {
    this.file = file;
    this.status = status;
    this.startedAt = startedAt;
    this.endedAt = endedAt;
    this.worker = worker;
  }

  public void setEndedAt(Instant endedAt) {
    this.endedAt = endedAt;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void setFile(Metadata file) {
    this.file = file;
  }

  public enum Status {
    IN_PROGRESS,
    COMPLETED,
    SKIPPED,
    FAILED
  }
}

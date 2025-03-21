package com.practice.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "metadata")
public class Metadata {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String name;

  @Column(nullable = false, length = 100)
  private String contentType;

  @Column(nullable = false)
  private long size;

  @CreatedDate
  @Column(nullable = false)
  private Instant createdAt;

  public Metadata(String name, String contentType, long size) {
    this.name = name;
    this.contentType = contentType;
    this.size = size;
    this.createdAt = Instant.now();
  }
}

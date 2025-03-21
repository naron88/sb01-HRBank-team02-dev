package com.practice.hrbank.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "employees")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(nullable = false)
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;

  @Column(nullable = false, length = 20)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String employeeNumber;

  @Column(nullable = false)
  private String position;

  @Column(nullable = false)
  private LocalDate hireDate;

  @Enumerated(EnumType.STRING)
  private Status status;

  @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "metadata_id")
  private Metadata profileImage;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "department_id", nullable = false)
  private Department department;

  public enum Status {
    ACTIVE,
    ON_LEAVE,
    RESIGNED;
  }

  public Employee(String name, String email, String employeeNumber, String position,
      LocalDate hireDate, Metadata profileImage, Department department) {
    this.name = name;
    this.email = email;
    this.employeeNumber = employeeNumber;
    this.position = position;
    this.hireDate = hireDate;
    this.status = Status.ACTIVE;

    this.profileImage = profileImage;
    this.department = department;
    this.createdAt = Instant.now();
  }

  public void updateName(String newName) {
    if (this.name.equals(newName)) {
        return;
    }
    this.name = newName;
  }

  public void updateEmail(String newEmail) {
    if (this.email.equals(newEmail)) {
      return;
    }
    this.email = newEmail;
  }

  public void updatePosition(String newPosition) {
    if (this.position.equals(newPosition)) {
      return;
    }
    this.position = newPosition;
  }

  public void updateDepartment(Department newDepartment) {
    if (this.department.getId().equals(newDepartment.getId())) {
      return;
    }
    this.department = newDepartment;
  }

  public void updateProfile(Metadata newProfileImage) {
    if (this.profileImage.getId().equals(newProfileImage.getId())) {
      return;
    }
    this.profileImage = newProfileImage;
  }

  public void updateStatus(Status newStatus) {
    if (this.status.equals(newStatus)) {
      return;
    }
    this.status = newStatus;
  }

  public void validateDuplicateEmail(String email) {
    if (this.email.equals(email)) {
      throw new IllegalArgumentException("Email must be unique");
    }
  }
}

package com.practice.hrbank.repository;

import com.practice.hrbank.entity.Backup;
import com.practice.hrbank.entity.Backup.Status;
import com.practice.hrbank.entity.Metadata;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Long>, JpaSpecificationExecutor<Backup> {

  Optional<Backup> findFirstByStatusOrderByStartedAtDesc(Status status);

  Optional<Backup> findByFile(Metadata metadata);
}

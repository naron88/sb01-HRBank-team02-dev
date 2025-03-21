package com.practice.hrbank.repository;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.practice.hrbank.config.JpaConfig;
import com.practice.hrbank.entity.Backup;
import com.practice.hrbank.entity.Backup.Status;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;

@DataJpaTest
@Import(JpaConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BackupRepositoryTest {

  @Autowired
  private BackupRepository backupRepository;

  @Test
  void 특정_상태의_백업만_조회된다() {
    Specification<Backup> spec = (root, query, cb) ->
        cb.equal(root.get("status"), Backup.Status.COMPLETED);

    List<Backup> results = backupRepository.findAll(spec);

    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getStatus()).isEqualTo(Backup.Status.COMPLETED);
  }

  @Test
  void 특정_날짜_범위의_백업만_조회된다() {
    Instant start = Instant.now().minusSeconds(86400); // 24시간 전
    Instant end = Instant.now();

    Specification<Backup> spec = (root, query, cb) ->
        cb.between(root.get("startedAt"), start, end);

    List<Backup> results = backupRepository.findAll(spec);

    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getStartedAt()).isAfterOrEqualTo(start);
    assertThat(results.get(0).getStartedAt()).isBeforeOrEqualTo(end);
  }

  @Test
  void 특정_작업자_IP의_백업만_조회된다() {
    String targetIp = "192.168.1.10"; // DB에 존재하는 IP

    Specification<Backup> spec = (root, query, cb) ->
        cb.equal(root.get("worker"), targetIp);

    List<Backup> results = backupRepository.findAll(spec);

    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getWorker()).isEqualTo(targetIp);
  }

  @Test
  void 상태와_날짜_범위와_작업자가_일치하는_백업만_조회된다() {
    Instant start = Instant.now().minusSeconds(86400);
    Instant end = Instant.now();
    String targetIp = "192.168.1.30";

    Specification<Backup> spec = Specification.where((Specification<Backup>) (root, query, cb) ->
            cb.equal(root.get("status"), Status.IN_PROGRESS))
        .and((Specification<Backup>) (root, query, cb) -> cb.between(root.get("startedAt"), start, end))
        .and((Specification<Backup>) (root, query, cb) -> cb.equal(root.get("worker"), targetIp));

    List<Backup> results = backupRepository.findAll(spec);

    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getStatus()).isEqualTo(Status.IN_PROGRESS);
    assertThat(results.get(0).getWorker()).isEqualTo(targetIp);
  }
}

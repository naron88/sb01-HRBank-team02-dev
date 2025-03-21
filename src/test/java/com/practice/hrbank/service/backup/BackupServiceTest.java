package com.practice.hrbank.service.backup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.practice.hrbank.dto.backup.BackupDto;
import com.practice.hrbank.entity.Backup;
import com.practice.hrbank.entity.Metadata;
import com.practice.hrbank.mapper.BackupMapper;
import com.practice.hrbank.repository.BackupRepository;
import com.practice.hrbank.service.BackupService;
import com.practice.hrbank.service.MetadataService;
import java.io.IOException;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BackupServiceTest {

  @Mock
  private BackupRepository backupRepository;

  @Mock
  private BackupMapper backupMapper;

  @Mock
  private MetadataService metadataService;

  @InjectMocks
  private BackupService backupService;

  @BeforeEach
  void setUp() {
    lenient().when(backupRepository.save(any(Backup.class))).thenAnswer(
        invocation -> invocation.getArgument(0));

    lenient().when(backupMapper.toDto(any(Backup.class))).thenAnswer(invocation -> {
      Backup b = invocation.getArgument(0);
      return new BackupDto(
          b.getId(),
          b.getWorker(),
          b.getStartedAt(),
          b.getEndedAt(),
          b.getStatus(),
          b.getFile() != null ? b.getFile().getId() : null
      );
    });
  }

  @Test
  void 생성_FAILED() throws IOException {
    // given
    when(metadataService.createEmployeesFile(any())).thenThrow(new IOException("Error"));

    // when
    BackupDto result = backupService.create("127.0.0.1");

    // then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(Backup.Status.FAILED);
  }

  @Test
  void 생성_COMPLETED() throws IOException {
    // given
    when(metadataService.createEmployeesFile(any())).thenReturn(new Metadata());

    // when
    BackupDto result = backupService.create("127.0.0.1");

    // then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(Backup.Status.COMPLETED);
  }

  @Test
  void 생성_SKIPPED() throws IOException {
    // given
    when(backupRepository.findFirstByStatusOrderByStartedAtDesc(Backup.Status.COMPLETED))
        .thenReturn(Optional.of(
            new Backup(new Metadata(), Backup.Status.COMPLETED, Instant.now(), Instant.now(),
                "127.0.0.1")));

    // when
    BackupDto result = backupService.create("127.0.0.1");

    // then
    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(Backup.Status.SKIPPED);
  }

  @Test
  void ID로_조회_성공() {
    // Given
    Backup backup = new Backup(null, Backup.Status.COMPLETED, Instant.now(), Instant.now(), "127.0.0.1");
    when(backupRepository.findById(1L)).thenReturn(Optional.of(backup));

    BackupDto backupDto = new BackupDto(1L, "127.0.0.1", backup.getStartedAt(), backup.getEndedAt(), backup.getStatus(), null);
    when(backupMapper.toDto(backup)).thenReturn(backupDto);

    // When
    BackupDto result = backupService.findById(1L);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.status()).isEqualTo(Backup.Status.COMPLETED);
    assertThat(result.worker()).isEqualTo(backup.getWorker());

    verify(backupRepository, times(1)).findById(1L);
    verify(backupMapper, times(1)).toDto(backup);
  }

  @Test
  void ID로_조회_실패() {
    // Given
    when(backupRepository.findById(2L)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> backupService.findById(2L))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContaining("Backup not found");

    verify(backupRepository, times(1)).findById(2L);
  }

}
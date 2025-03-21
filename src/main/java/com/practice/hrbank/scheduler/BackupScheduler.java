package com.practice.hrbank.scheduler;

import com.practice.hrbank.service.BackupService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BackupScheduler {
  private static final long ONE_HOUR = 1000 * 60 * 60;

  private final BackupService backupService;

  @Scheduled(fixedRate = ONE_HOUR)
  public void backup() throws IOException {
    backupService.create("system");
  }
}

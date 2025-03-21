package com.practice.hrbank.storage.local;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "hrbank.storage.local.paths.error-log-path=target/test-logs")
public class LocalLogFileStorageTest {

  @Autowired
  private LocalLogFileStorage localLogFileStorage;

  private Path testDirectory;

  @BeforeEach
  public void setUp() throws IOException {
    testDirectory = Path.of("target", "test-logs");
    if (Files.notExists(testDirectory)) {
      Files.createDirectories(testDirectory);
    }
  }

  @Test
  public void testCreateLogFile() throws IOException {
    // given
    Instant time = Instant.now();
    String errorMessage = "test";
    Path path = testDirectory.resolve(time.toString().replace(":", "-") + ".log");

    // when
    localLogFileStorage.createFile(time, errorMessage);

    // then
    assertTrue(Files.exists(path));
  }
}

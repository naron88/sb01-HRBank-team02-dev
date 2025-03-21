package com.practice.hrbank.storage.local;

import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.file.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "hrbank.storage.local.paths.binary-content-path=target/test-files")
public class LocalBinaryContentStorageTest {

  @Autowired
  private LocalBinaryContentStorage localBinaryContentStorage;

  private Path testDirectory;

  @BeforeEach
  public void setUp() throws IOException {
    testDirectory = Path.of("target", "test-files");
    if (Files.notExists(testDirectory)) {
      Files.createDirectories(testDirectory);
    }
  }

  @Test
  public void createFile_Success() throws IOException {
    // given
    Long id = 2L;
    byte[] data = "test2_profile".getBytes();
    Path filePath = testDirectory.resolve(id.toString());

    // when
    localBinaryContentStorage.createFile(id, data);

    // then
    assertTrue(Files.exists(filePath));
  }
}


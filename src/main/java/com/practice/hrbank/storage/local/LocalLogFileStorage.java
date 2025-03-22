package com.practice.hrbank.storage.local;

import jakarta.annotation.PostConstruct;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "hrbank.storage.type", havingValue = "local")
public class LocalLogFileStorage {

  private final Path root;

  public LocalLogFileStorage(@Value("${hrbank.storage.local.paths.error-log-path}") String root) {
    this.root = Paths.get(root);
  }

  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(root);
  }

  public Long createFile(Instant time, String errorMessage) throws IOException{
    Path logFilePath = root.resolve(time.toString().replace(":", "-") + ".log");
    try (BufferedWriter writer = Files.newBufferedWriter(logFilePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      writer.write("Error: " + errorMessage);
      writer.newLine();
    }
    return Files.size(logFilePath);
  }
}

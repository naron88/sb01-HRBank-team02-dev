package com.practice.hrbank.storage.local;

import com.practice.hrbank.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "hrbank.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(
      @Value("${hrbank.storage.local.paths.binary-content-path}") String root) {
    this.root = Paths.get(root);
  }

  @PostConstruct
  public void init() throws IOException {
    Files.createDirectories(root);
  }

  @Override
  public Long createFile(Long id, byte[] bytes) throws IOException{
    Path filePath = root.resolve(id.toString());
    try (OutputStream outputStream = Files.newOutputStream(filePath)) {
      outputStream.write(bytes);
    }
    return id;
  }
}

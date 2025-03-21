package com.practice.hrbank.storage;

import java.io.IOException;

public interface BinaryContentStorage {

  Long createFile(Long id, byte[] bytes) throws IOException;
}

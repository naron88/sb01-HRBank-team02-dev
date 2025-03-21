package com.practice.hrbank.util;

import java.util.Base64;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class CursorPaginationUtils {

  private CursorPaginationUtils() {
  }

  public static Pageable createPageable(Integer size, String sortField, String sortDirection,
      String defaultSortField, String defaultSortDirection) {
    if (size == null || size <= 0) {
      size = 10;
    }

    Sort sort;
    Direction direction;
    if (sortField != null && !sortField.isEmpty()) {
      direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
      sort = Sort.by(direction, sortField);
    } else {
      direction = "desc".equalsIgnoreCase(defaultSortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
      sort = Sort.by(direction, defaultSortField);
    }

    return PageRequest.of(0, size, sort);
  }

  public static String encodeCursor(Long cursor) {
    if (cursor != null) {
      return Base64.getEncoder().encodeToString(cursor.toString().getBytes());
    }
    return null;
  }

  public static Long decodeCursor(String cursor) {
    if (cursor != null) {
      try {
        String decoded = new String(Base64.getDecoder().decode(cursor));
        return Long.parseLong(decoded);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid cursor format");
      }
    }
    return null;
  }
}

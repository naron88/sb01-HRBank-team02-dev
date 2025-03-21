package com.practice.hrbank.service.metadata;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.practice.hrbank.entity.Department;
import com.practice.hrbank.entity.Employee;
import com.practice.hrbank.entity.Employee.Status;
import com.practice.hrbank.entity.Metadata;
import com.practice.hrbank.repository.EmployeeRepository;
import com.practice.hrbank.repository.MetadataRepository;
import com.practice.hrbank.service.MetadataService;
import com.practice.hrbank.storage.BinaryContentStorage;
import com.practice.hrbank.storage.EmployeesStorage;
import com.practice.hrbank.storage.local.LocalLogFileStorage;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class MetadataServiceTest {

  @InjectMocks
  private MetadataService metadataService;

  @Mock
  private MetadataRepository metadataRepository;

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private EmployeesStorage employeesStorage;

  @Mock
  private LocalLogFileStorage localLogFileStorage;

  @Test
  void createProfile_Success() throws IOException {
    // given
    MultipartFile profile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg",
        "dummy".getBytes());
    Metadata metadata = new Metadata(profile.getName(), profile.getContentType(),
        profile.getSize());

    // when
    when(metadataRepository.save(any(Metadata.class))).thenReturn(metadata);
    Metadata result = metadataService.createProfile(profile);

    // then
    assertNotNull(result);
    assertEquals(profile.getName(), result.getName());
    verify(binaryContentStorage, times(1)).createFile(any(), any());
    verify(metadataRepository, times(1)).save(any(Metadata.class));
  }

  @Test
  void createEmployeesFile_Success() throws IOException {
    // given - 테스트 할 때만 Employee 생성자를 바꾸고 했습니다.
    Employee emp1 = new Employee(1L, "test1", "test1@gmail.com", "E-1", "Manager", LocalDate.now(),
        new Metadata("test1_profile", "impage/jpg", 1024L), new Department("IT", "IT 부서", LocalDate.now(), 1));
    Employee emp2 = new Employee(2L, "test2", "test2@gmail.com", "E-2", "Manager", LocalDate.now(),
        new Metadata("test2_profile", "impage/jpg", 2048L), new Department("마케팅", "마케팅 부서", LocalDate.now(), 2));
    List<Employee> employees = asList(emp1, emp2);
    Long backupId = 3L;
    Long fileSize = 1024L;

    // when
    when(employeeRepository.findAll()).thenReturn(employees);
    when(employeesStorage.save(backupId, employees)).thenReturn(fileSize);
    Metadata result = metadataService.createEmployeesFile(backupId);

    // then
    assertNotNull(result);
    assertEquals("employee_backup_" + backupId, result.getName());
    assertEquals("text/csv", result.getContentType());
    assertEquals(fileSize, result.getSize());
    verify(employeeRepository, times(1)).findAll();  // Ensure that findAll was called
    verify(employeesStorage, times(1)).save(backupId, employees);
  }

  @Test
  void createErrorLogFile_Success() throws IOException {
    // given
    Instant time = Instant.now();
    String errorMessage = "error test";
    long mockedFileSize = 1024L;  // Mocked file size

    // when
    when(localLogFileStorage.createFile(time, errorMessage)).thenReturn(mockedFileSize);
    Metadata result = metadataService.createErrorLogFile(time, errorMessage);

    // then
    assertNotNull(result);
    assertEquals(time.toString(), result.getName());
    assertEquals("text/log", result.getContentType());
    assertEquals(mockedFileSize, result.getSize());
    verify(localLogFileStorage, times(1)).createFile(time, errorMessage);
  }

  @Test
  void findById_Success() {
    // given
    Long id = 1L;
    Metadata metadata = new Metadata("test.jpg", "image/jpeg", 1024L);

    // when
    when(metadataRepository.findById(id)).thenReturn(Optional.of(metadata));
    Metadata result = metadataService.findById(id);

    // then
    assertNotNull(result);
    assertEquals(metadata.getName(), result.getName());
    verify(metadataRepository, times(1)).findById(id);
  }

  @Test
  void findById_NotFoundId() {
    // given
    Long id = 1L;

    // when
    when(metadataRepository.findById(id)).thenReturn(Optional.empty());

    // then
    assertThrows(NoSuchElementException.class, () -> metadataService.findById(id));
    verify(metadataRepository, times(1)).findById(id);
  }
}
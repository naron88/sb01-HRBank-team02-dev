CREATE TABLE metadata
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size         BIGINT       NOT NULL,
    created_at   TIMESTAMP    NOT NULL
);

CREATE TABLE departments
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    description      TEXT         NOT NULL,
    established_date DATE         NOT NULL,
    created_at       TIMESTAMP    NOT NULL,
    updated_at       TIMESTAMP
);

CREATE TABLE change_logs
(
    id              BIGSERIAL                PRIMARY KEY,
    type            VARCHAR(10)  NOT NULL CHECK (type IN ('CREATED', 'UPDATED', 'DELETED')),
    employee_number VARCHAR(30)  NOT NULL,
    detail          TEXT NOT NULL,
    memo            VARCHAR(255) NOT NULL DEFAULT '직원 정보 수정',
    ip_address      VARCHAR(20)  NOT NULL,
    at              TIMESTAMP    NOT NULL
);

CREATE TABLE backups
(
    id          BIGSERIAL PRIMARY KEY,
    metadata_id BIGINT,
    worker      VARCHAR(50) NOT NULL,
    started_at  TIMESTAMP   NOT NULL,
    ended_at    TIMESTAMP,
    status      VARCHAR(20) NOT NULL CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'FAILED', 'SKIPPED')),
    CONSTRAINT fk_backup_metadata FOREIGN KEY (metadata_id) REFERENCES metadata (id)
);

CREATE TABLE employees
(
    id              BIGSERIAL PRIMARY KEY,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP,
    metadata_id     BIGINT,
    department_id   BIGINT       NOT NULL,
    name            VARCHAR(20)  NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    employee_number VARCHAR(255) NOT NULL,
    position        VARCHAR(255) NOT NULL,
    hire_date       DATE         NOT NULL,
    status          VARCHAR(10)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'ON_LEAVE', 'RESIGNED')),
    CONSTRAINT fk_employee_department FOREIGN KEY (department_id) REFERENCES departments (id),
    CONSTRAINT fk_employee_metadata FOREIGN KEY (metadata_id) REFERENCES metadata (id) ON DELETE CASCADE
);
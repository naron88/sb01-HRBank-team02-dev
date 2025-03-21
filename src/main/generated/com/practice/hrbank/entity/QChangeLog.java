package com.practice.hrbank.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChangeLog is a Querydsl query type for ChangeLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChangeLog extends EntityPathBase<ChangeLog> {

    private static final long serialVersionUID = 393966283L;

    public static final QChangeLog changeLog = new QChangeLog("changeLog");

    public final DateTimePath<java.time.Instant> at = createDateTime("at", java.time.Instant.class);

    public final StringPath detail = createString("detail");

    public final StringPath employeeNumber = createString("employeeNumber");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ipAddress = createString("ipAddress");

    public final StringPath memo = createString("memo");

    public final EnumPath<ChangeLog.Type> type = createEnum("type", ChangeLog.Type.class);

    public QChangeLog(String variable) {
        super(ChangeLog.class, forVariable(variable));
    }

    public QChangeLog(Path<? extends ChangeLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChangeLog(PathMetadata metadata) {
        super(ChangeLog.class, metadata);
    }

}


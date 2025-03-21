package com.practice.hrbank.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMetadata is a Querydsl query type for Metadata
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMetadata extends EntityPathBase<Metadata> {

    private static final long serialVersionUID = -207144200L;

    public static final QMetadata metadata = new QMetadata("metadata");

    public final StringPath contentType = createString("contentType");

    public final DateTimePath<java.time.Instant> createdAt = createDateTime("createdAt", java.time.Instant.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> size = createNumber("size", Long.class);

    public QMetadata(String variable) {
        super(Metadata.class, forVariable(variable));
    }

    public QMetadata(Path<? extends Metadata> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMetadata(PathMetadata metadata) {
        super(Metadata.class, metadata);
    }

}


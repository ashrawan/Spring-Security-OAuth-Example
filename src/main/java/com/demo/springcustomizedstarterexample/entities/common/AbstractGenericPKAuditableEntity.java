package com.demo.springcustomizedstarterexample.entities.common;

import com.demo.springcustomizedstarterexample.entities.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AbstractGenericPKAuditableEntity<PK extends Serializable> extends AbstractGenericPrimaryKey<PK> {

    @Nullable
    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private UserEntity createdBy;

    @Nullable
    @CreatedDate
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;

    @Nullable
    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "last_modified_by_id")
    private UserEntity lastModifiedBy;

    @Nullable
    @LastModifiedDate
    @Column(name = "last_modified_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastModifiedDate;
}

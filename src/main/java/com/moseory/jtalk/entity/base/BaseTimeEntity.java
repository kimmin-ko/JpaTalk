package com.moseory.jtalk.entity.base;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@ToString
@MappedSuperclass
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdDate;

    @LastModifiedDate
    protected LocalDateTime modifiedDate;

}

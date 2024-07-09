package org.zerock.b01.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// 엔티티 클래스들이 공통으로 가지는 매핑 정보를 상속받을 수 있도록 한다.
// 여러 엔티티 클래스에 공통적으로 사용되는 매핑 정보를 하나의 클래스에 정의하고, 이를 상속받는 방식을 코드 중복을 줄인다.
@MappedSuperclass
// 엔티티 리스너를 통해 엔티티의 특정 이벤트(생성, 수정 등)을 처리할 수 있도록 한다.
// AuditingEntityListener.class : 엔티티의 생성 및 수정 날짜를 자동으로 관리
@EntityListeners(value = {AuditingEntityListener.class})
@Getter
abstract class BaseEntity {
    @CreatedDate // 엔티티가 처음 저장될 때 생성일을 자동 설정
    // updatable = false : 테이블이 수정될 때 해당 필드는 변경되지 않도록 설정
    @Column(name = "regdate", updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate // 엔티티가 수정될 때 수정일을 자동으로 설정
    @Column(name = "moddate")
    private LocalDateTime modDate;
}

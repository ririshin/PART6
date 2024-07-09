package org.zerock.b01.domain;

import jakarta.persistence.*;
import lombok.*;

/*
    JPA 연관 관계 구성 시 아래의 부분을 주의해서 작성해야 한다.
    - @ToString 생성 시 참조하는 객체를 사용하지 않도록 exclude 속성으로 참조 객체 필드는 제외한다.
    - @ManyToOne과 같이 연관 관게를 나타낼 때는 반드시 fetch 속성을 LAZY로 지정하여 불필요한 데이터를 로딩하지 않도록 한다.
 */

@Entity // -- 해당 클래스가 JPA 엔티티임을 나타냄
@Table(name = "Reply", indexes = {
        @Index(name = "idx_reply_board_bno", columnList = "board_bno")
})
@Getter // 모든 필드의 getter 메서드 자동 생성
@Builder // Builder 패턴 사용하여 객체 생성
@AllArgsConstructor // 모든 필드 매개변수로 가지는 생성자 생성
@NoArgsConstructor // 매개변수가 없는 기본 생성자 생성
@ToString(exclude = "board") // toString() 메서드 생성하되 "board" 필드는 제외(exclude)
public class Reply extends BaseEntity{

    @Id // 해당 필드가 엔티티의 기본 키임을 나타냄
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 자동 생성 전략
    private Long rno;


    /*
        fetch?
        - 관계를 가져오는 방식을 설정

        ** FetchType.LAZY ** 지연 로딩
            - 관계된 엔티티를 실제로 사용하기 전까지 로딩하지 않는다.
            - 성능 최적화에 도움이 되며, 필요하지 않은 데이터를 불필요하게 로딩하지 않도록 한다.
              현재 예제에 맞춘 설명
                -> Reply 엔티티를 조회할 때 Board 엔티티는 조회하지 X
                -> Reply 엔티티의 getBoard() 메서드를 호출해야 Board 엔티티를 조회한다.

        ** FetchType.EAGER ** 즉시 로딩
            - 엔티티를 조회활 때 관계된 엔티티도 함께 즉시 로딩한다.
            - 불필요한 데이터를 미리 로딩하게 되어 성능에 부정적인 영향이 있을 수 있다.
              현재 예제에 맞춘 설명
                -> Reply 엔티티 조회 시, Board 엔티티도 즉시 로딩된다.
     */
    @ManyToOne(fetch = FetchType.LAZY) // 다른 엔티티(Board)와 다대일 관계가 있음을 나타냄
    private Board board;

    private String replyText;

    private String replyer;

    public void changeText(String text) {
        this.replyText = text;
    }

}

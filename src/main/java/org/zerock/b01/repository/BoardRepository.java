package org.zerock.b01.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.Board;
import org.zerock.b01.repository.search.BoardSearch;


// JPA 레포지토리로, 데이터베이스와 상호작용하는 인터페이스 => 보통 JpaRepository를 상속받아 CRUD(Create, Read, Update, Delete) 작업을 수행
public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {

    // Query Method : 메서드 이름을 기반으로 쿼리를 자동 생성 (규칙에 따라 작성하여 다양한 조건 표현)
    // @Query : JPQL 또는 native SQL 쿼리를 직접 작성하여 복잡한 쿼리 정의

    // Query Method
    /*
       Spring-data-jpa에서 제공하는 기능으로, 메소드 이름을 기반으로 쿼리를 자동으로 생성하고 실행
       쿼리 메소드의 이름은 특정한 규칙을 따르며, 이를 통해 필요한 쿼리를 표현할 수 있다.

       1. findBy : 쿼리를 시작하는 키워드로, 뒤에 오는 속성 이름을 기반으로 쿼리를 생성
       2. 속성 이름 : 엔티티 클래스의 "필드 이름"을 사용
            -> 예를 들어 title, content 등 해당하는 필드를 기준으로 작성
       3. 조건 키워드 : And, Or, Between, LessThan, GreaterThan, Like, Containing, OrderBy 등 다양한 조건 키워드를 조합하여 복잡한 쿼리 생성 가능
     */


    Page<Board> findByTitleContainingOrderByBnoDesc(String keyword, Pageable pageable);


    // @Query
    /*
        복잡한 쿼리를 직접 작성해야 할 때 사용. JPQL을 통해 쿼리를 정의한다. (네이티브 SQL도 사용은 가능)
        JPQL?
            - 엔티티 객체를 대상으로 하는 객체지향 쿼리 언어
            - SQL과 유사하지만, 테이블이 아닌 엔티티 클래스를 대상으로 사용
        네이티브 SQL?
            - 데이터베이스에 종속적인 쿼리를 작성하여 데이터베이스 테이블을 다루는 SQL 작성
            - 복잡한 조인이나 특정 DBMS 기능을 사용할 때 유용

        * 조인과 같이 복잡한 쿼리를 실행할 수 있는 기능
        * 원하는 속성들만 추출하여 Object[]로 처리하거나 DTO로 처리 가능
        * nativeQuery 속성값을 true로 지정해 특정 데이터베이스에 동작하는(종속적) SQL을 사용하는 기능
     */
    @Query("select b from Board b where b.title like concat('%', ':keyword', '%')")
    Page<Board> findKeyword(String keyword, Pageable pageable);

    // native 속성을 지정하는 예제
    @Query(value = "select now()", nativeQuery = true)
    String getTime();
}

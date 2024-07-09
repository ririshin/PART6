package org.zerock.b01.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.QBoard;
import org.zerock.b01.domain.QReply;
import org.zerock.b01.dto.BoardListReplyCountDTO;

import java.util.List;

/*
    QueryDSL이 엔티티 클래스를 기반으로 생성한 메타 데이터 클래스 -- QEntityName
        이를 통해 타입 세이프하고, 컴파일 타임에 오류를 잡을 수 있는 쿼리 작성 가능

        Type-safe?
            프로그래밍에서 데이터 타입을 엄격하게 검사하여 잘못된 타입의 데이터가 사용되지 않도록 보장하는 것을 의미 -- 주로 컴파일 타임에 이루어지며, 잘못된 타입 사용으로 인한 오류를 방지

    [주요 구성 요소]
        Q클래스?
            Q 클래스는 엔티티의 각 필드에 접근할 수 있는 정적 필드를 제공
            주로 querydsl-apt 라이브러리에 의해 컴파일 타임에 자동 생성
        JPAQueryFactory
            QueryDSL 쿼리를 생성하는 팩토리 클래스, EntityManager를 통해 초기화
            -- 이를 통해 쿼리를 작성하고 실행 가능
        BooleanBuilder
            조건을 동적으로 조합할 때 사용
            -- 이를 통해 여러 조건을 AND 또는 OR로 연결하여 동적 쿼리 작성
        PathBuilder
            동적 경로를 표현하기 위해 사용, 문자열로 필드를 지정하여 쿼리 구성 가능
 */

/*
    QueryDSL 주요 메서드
    1. 기본 메서드
        select() / from() / where()
        fetch() -- 쿼리 실행하여 결과 "리스트"로 반환
        fetchOne() -- 쿼리 실행하여 결과를 "단일 객체"로 반환
        fetchCount() -- 쿼리 결과의 개수 반환

    2. 조건 작성
        eq() -- = / ne() -- != / gt() -- > / lt() -- < / goe() -- >= / loe() -- <=
        like() -- LIKE 조건 / contains() -- 부분 문자열 검색 조건 지정
        in() -- IN 조건 지정 / notIn() -- NOT IN 조건 지정
        isNull() -- NULL 여부 체크 / isNotNull -- NULL이 아님 체크

    3. 정렬 및 페이징
        orderBy() -- 정렬 조건 지정
        limit() -- 결과 최대 개수 지정
        offset() -- 결과의 시작 위치 지정
*/



public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    /*
        QuerydslRepositorySupport?
            Spring Data JPA와 QueryDSL을 통합하여 사용할 때 유용한 유틸리티 클래스
            -- 쿼리를 쉽게 작성하고 실행할 수 있는 다양한 메서드 제공
            
     */
    public BoardSearchImpl() {
        super(Board.class); // -- QuerydslRepositorySupport 생성자 호출 + Board.class 매개변수로 전달
        // -- Board 엔티티와 관련된 작업을 수행할 수 있게 객체 생성
    }

    // Pageable 객체를 받아 페이징된 결과를 반환하는 메서드
    @Override
    public Page<Board> search1(Pageable pageable) {

        // QBoard 객체는 QueryDSL이 `Board` 엔티티를 기반으로 생성한 Q 클래스 -- 타입 세이프한 쿼리 작성
        QBoard board = QBoard.board; // Q도메인 객체

        // from(board) => Board 엔티티를 대상으로 하는 JPQL 쿼리를 생성 (SELECT ... FROM board와 유사)
        JPQLQuery<Board> query = from(board); // select... from board

        // BooleanBuilder를 사용하여 여러 조건을 조합 (OR, AND 등) : '()'로 묶는다
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // [OR 조건 추가]
        // title 필드가 11을 포함하는 조건 추가
        booleanBuilder.or(board.title.contains("11"));

        // content 필드가 11을 포함하는 조건 추가
        booleanBuilder.or(board.content.contains("11"));

        query.where(booleanBuilder); // -- 최종적으로 booleanBuilder를 where 절에 추가

        // bno > 0 -- bno 필드가 0보다 큰 레코드를 조회하는 기본 조건 추가 -- 앞선 조건과 AND로 결합
        query.where(board.bno.gt(0L));

/*        // where(board.title.contains("1") => title 필드가 1을 포함하는 레코드를 조회하는 건을 추가 (WHERE title LIKE '%1%')
        query.where(board.title.contains("1")); // where title like...*/

        // paging -- applyPagination()를 활용하여 pageable 객체를 쿼리에 적용 (pageable : 페이지 번호와 페이지 크기 등 페이징 정보가 반영)
        this.getQuerydsl().applyPagination(pageable, query);

        // 쿼리 실행 및 결과 조회
        // fetch() -- 쿼리 실행
        List<Board> list = query.fetch();

        // fetchCount() -- 쿼리를 실행하여 조건에 맞는 전체 레코드 수를 반환
        long count = query.fetchCount();

        return null;
    }

    // 검색 조건과 키워드 기반으로 Board 엔티티의 데이터를 페이징 처리하여 조회하는 예제
    // types[], keyword를 사용하여 동적 검색 조건을 적용하고, 페이징 처리를 수행
    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);

        // [동적 검색 조건] : 검색 조건(types)과 키워드(keyword)가 있다면,
        if ((types != null) && (types.length > 0) && keyword != null) {

            // BooleanBuilder를 사용하여 OR 조건을 조합
            BooleanBuilder booleanBuilder = new BooleanBuilder(); // ()

            // types[] 각 요소에 대해 switch를 사용하여 title, content, writer 필드에 대해 조건을 추가
            for (String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            } // end for

            query.where(booleanBuilder); // -- 최종적으로 booleanBuilder를 where 절에 추가

        } // end if

        // bno > 0 -- bno 필드가 0보다 큰 레코드를 조회하는 기본 조건 추가 -- 앞선 조건과 AND로 결합
        query.where(board.bno.gt(0L));

        // paging -- applyPagination()를 활용하여 pageable 객체를 쿼리에 적용 (pageable : 페이지 번호와 페이지 크기 등 페이징 정보가 반영)
        this.getQuerydsl().applyPagination(pageable, query);

        // fetch() -- 쿼리 실행
        List<Board> list = query.fetch();

        // fetchCount() -- 쿼리를 실행하여 조건에 맞는 전체 레코드 수를 반환
        long count = query.fetchCount();

        // List<T> : 실제 목록 데이터
        // Pageable : 페이지 관련 정보를 가진 객체
        // long : 전체 개수
        return new PageImpl<>(list, pageable, count); // -- 페이징된 데이터 목록, 페이지 정보, 전체 레코드 수를 포함
    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {

        // Board, Reply 엔티티 JPQL 쿼리 사용 위해 QBoard, QReply 객체 생성
        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        // 기본 JPQL 쿼리 생성
        JPQLQuery<Board> query = from(board); // select * from Board
        // Reply 엔티티를 Board 엔티티에 Left join
        // on(조인절 조건) -- Reply 엔티티의 board 필드가 Board 엔티티의 기본 키와 일치하는 경우에만 조인
        /*
            Left Join
            왼쪽 테이블(Board)의 모든 레코드와 오른쪽 테이블(Reply)의 매칭되는 레코드를 반환 (매칭되는 레코드가 없으면 오른쪽 테이블 값은 NULL)
         */
        query.leftJoin(reply).on(reply.board.eq(board));

        query.groupBy(board); // -- Board 엔티티를 기준으로 그룹화

        // [동적 검색 조건] : 검색 조건(types)과 키워드(keyword)가 있다면,
        if ((types != null) && (types.length > 0) && keyword != null) {

            // BooleanBuilder를 사용하여 OR 조건을 조합
            BooleanBuilder booleanBuilder = new BooleanBuilder(); // ()

            // types[] 각 요소에 대해 switch를 사용하여 title, content, writer 필드에 대해 조건을 추가
            for (String type : types) {
                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            } // end for

            query.where(booleanBuilder); // -- 최종적으로 booleanBuilder를 where 절에 추가

        } // end if

        // bno > 0 -- bno 필드가 0보다 큰 레코드를 조회하는 기본 조건 추가 -- 앞선 조건과 AND로 결합
        query.where(board.bno.gt(0L));

        // 검색 결과를 BoardListReplyCountDTO 객체로 변환 (Projections.bean())
        JPQLQuery<BoardListReplyCountDTO> dtoQuery = query.select(Projections.bean(BoardListReplyCountDTO.class,
                board.bno,
                board.title,
                board.writer,
                board.regDate,
                reply.count().as("replyCount") // -- 댓글 수를 replyCount 필드에 매핑
        ));

        // paging -- applyPagination()를 활용하여 pageable 객체를 쿼리에 적용 (pageable : 페이지 번호와 페이지 크기 등 페이징 정보가 반영)
        this.getQuerydsl().applyPagination(pageable, dtoQuery);

        // fetch() -- 쿼리 실행
        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch();

        // fetchCount() -- 쿼리를 실행하여 조건에 맞는 전체 레코드 수를 반환
        long count = dtoQuery.fetchCount();

        // List<T> : 실제 목록 데이터
        // Pageable : 페이지 관련 정보를 가진 객체
        // long : 전체 개수
        return new PageImpl<>(dtoList, pageable, count); // -- 페이징된 데이터 목록, 페이지 정보, 전체 레코드 수를 포함
    }

}

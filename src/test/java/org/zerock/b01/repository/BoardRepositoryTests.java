package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void testInsert() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Board board = Board.builder()
                    .title("title..."+i)
                    .content("content..."+i)
                    .writer("user"+(i%10))
                    .build();

            Board result = boardRepository.save(board);
            log.info("BNO : {}", result.getBno());
        });
    }

    @Test
    public void testSelect() {
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow();
        log.info(board);
    }

    @Test
    public void testUpdate() {
        Long bno = 100L;
        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow();

        board.change("update..title 100", "update...content 100");

        boardRepository.save(board);
    }

    @Test
    public void testDelect() {
        Long bno = 1L;
        boardRepository.deleteById(bno);
    }

    @Test
    public void testPaging() {

        // Pageable Interface : 페이징 처리와 관련된 정보를 캡슐화하고 있는 인터페이스 (페이지 번호, 페이지 크기, 정렬 순서 등의 정보 포함)
        // PageRequest : Pageable 인터페이스를 구현한 구현체
        /*
            정적 메서드 of를 통해 쉽게 인스턴스 생성 가능
            받고 있는 매개변수 (int page(요청할 페이지 번호), int size(페이지당 항목 수), [Sort sort(정렬 기준)])
         */

        // 1 page order by bno desc => 첫 번째 페이지(0)에서 10개의 항목을 bno 필드를 기준으로 desending(내림차순) 정렬하여 가져오도록 객체 생성
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        // Page<T> Interface : 페이징 처리된 결과를 나타내는 인터페이스 (페이징된 데이터와 관련된 메타데이터(전체 페이지 수, 현재 페이지 번호 등)을 포함
        Page<Board> result = boardRepository.findAll(pageable);

        log.info("total count : {}", result.getTotalElements()); // 전체 데이터 개수 반환 (데이터베이스에 저장된 총 Board 엔티티의 개수)
        log.info("total pages : {}", result.getTotalPages()); // 전체 페이지 수
        log.info("page number : {}", result.getNumber()); // 현재 페이지 번호 -- 페이지 번호는 0번부터 시작
        log.info("page size : {}", result.getSize()); // 현재 페이지에서 조회하고 있는 데이터 수(페이지 크기)

        List<Board> todoList = result.getContent(); // 현재 페이지에 있는 Board 엔티티 리스트를 반환 (페이징된 실제 데이터)

        todoList.forEach(log::info);

    }

    @Test
    public void testSearch1() {
        // 2 page order by bno desc
        Pageable pageable = PageRequest.of(1, 10, Sort.by("bno").descending());

        boardRepository.search1(pageable);
    }


    @Test
    public void testSearchAll() {
        String[] types = {"t", "c", "w"}; // -- 검색 조건
        String keyword = "1"; // 검색 키워드
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending()); // -- 페이징 (첫 페이지(0)에 10개 항목을 bno 필드 기준으로 내림차순 정렬
        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);
    }

    @Test
    public void testSearchAll2() {
        String[] types = {"t", "c", "w"};
        String keyword = "1";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());
        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        
        // [페이징 정보 로그 출력]
        log.info(result.getTotalPages()); // -- total pages
        
        log.info(result.getSize()); // -- page size (한 페이지에 표시되는 항목 수)

        log.info(result.getNumber()); // -- pageNumber (현재 페이지 번호)
        
        log.info("{}: {}", result.hasPrevious(), result.hasNext()); // -- prev next 존재 여부

        result.getContent().forEach(log::info); // -- 검색 결과 목록을 각각 출력
    }

    @Test
    public void testSearchReplyCount() {
        String[] types = {"t", "c", "w"};
        String keyword = "1";
        Pageable pageable = PageRequest.of(1, 10, Sort.by("bno").descending());
        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);


        // [페이징 정보 로그 출력]
        log.info(result.getTotalPages()); // -- total pages

        log.info(result.getSize()); // -- page size (한 페이지에 표시되는 항목 수)

        log.info(result.getNumber()); // -- pageNumber (현재 페이지 번호)

        log.info("{}: {}", result.hasPrevious(), result.hasNext()); // -- prev next 존재 여부

        result.getContent().forEach(log::info); // -- 검색 결과 목록을 각각 출력
    }
}
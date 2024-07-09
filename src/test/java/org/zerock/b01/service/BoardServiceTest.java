package org.zerock.b01.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.dto.BoardDTO;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // -- Spring Boot App-Context를 로드하여 통합 테스트 수행
@Log4j2
class BoardServiceTest {
    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister() {
        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("Sample Title...")
                .content("Sample Content...")
                .writer("user00")
                .build();

        Long bno = boardService.register(boardDTO);

        log.info("bno : {}", bno);
    }

    @Test
    public void testModify() {
        // 변경 필요한 데이터만
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(101L)
                .title("Update...101")
                .content("Update Content 101...")
                .build();

        boardService.modify(boardDTO);
    }

    @Test
    public void testList() { // -- 목록/검색 기능 확인
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();
        // -- 제목 혹은 내용 혹은 작성자가 '1'이라는 문자열을 가진 데이터를 검색하고 페이징 처리

        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        log.info(responseDTO);
    }
}
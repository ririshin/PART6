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
import org.zerock.b01.domain.Reply;

@SpringBootTest
@Log4j2
class ReplyRepositoryTest {

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    public void testInsert() {
        // 실제 DB에 있는 bno
        Long bno = 100L;

        Board board = Board.builder().bno(bno).build();

        Reply reply = Reply.builder()
                .board(board)
                .replyText("댓글.....")
                .replyer("replyer1")
                .build();

        replyRepository.save(reply);
    }

    @Test
    public void testBoardReplies() {
        Long bno = 100L; // -- 테스트 대상이 될 게시글

        // 첫번째 페이지(0)에서 10개의 댓글을, rno 기준 내림차순으로 정렬하여 요청
        Pageable pageable = PageRequest.of(0, 10, Sort.by("rno").descending());

        // 특정 게시글(bno)에 대한 댓글 목록을 페이지네이션하여 조회
        // 결과는 Page<Reply> 객체로 반환 : 조회된 댓글 목록과 페이지 정보를 포함
        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        // getContent() : 페이지 내의 Reply 객체들을 List로 반환
        // forEach(log::info) : 반환 받은 List 원소에 하나씩 접근하여 log에 출력
        result.getContent().forEach(log::info);
    }

}
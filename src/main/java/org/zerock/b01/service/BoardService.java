package org.zerock.b01.service;

import org.zerock.b01.dto.BoardDTO;
import org.zerock.b01.dto.BoardListReplyCountDTO;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;

public interface BoardService {
    Long register(BoardDTO boardDTO); // -- 등록
    BoardDTO readOne(Long bno); // -- 특정 게시물 조회
    void modify(BoardDTO boardDTO); // -- 수정 (기존 엔티티 객체에서 필요한 부분만 변경)
    void remove(Long bno); // -- 삭제
    PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO);
    PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO);


}

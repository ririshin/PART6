package org.zerock.b01.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
// -- 게시글 목록 화면에 필요한 데이터 객체 DTO
public class BoardListReplyCountDTO {
    private Long bno;
    private String title;
    private String writer;
    private LocalDateTime regDate; // -- 등록 일자
    private Long replyCount; // 해당 게시글에 달린 댓글 수
}

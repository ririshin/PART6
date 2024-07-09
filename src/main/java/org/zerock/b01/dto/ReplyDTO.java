package org.zerock.b01.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {
    private Long rno; // 댓글 번호

    @NotNull
    private Long bno; // 댓글이 달린 특정 게시물 번호

    @NotEmpty
    private String replyText; // 댓글 내용

    @NotEmpty
    private String replyer; // 댓글 작성자

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate; // 댓글 등록

    @JsonIgnore
    private LocalDateTime modDate; // 댓글 수정일
}

/*
    @JsonFormat?
        Jackson 라이브러리에서 사용되며, 날짜와 시간 형식을 지정할 수 있음
        pattern 속성을 통해 원하는 날짜 형식을 지정

    @JsonIgnore
        Jackson 라이브러리에서 사용되며, 특정 필드를 JSON 직렬화 및 역질렬화 과정에서 무시하도록 함
        특정 필드를 JSON 출력에서 제외하거나 입력 무시
 */

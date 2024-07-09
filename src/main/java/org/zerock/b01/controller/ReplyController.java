package org.zerock.b01.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.service.ReplyService;

import java.util.HashMap;
import java.util.Map;

/*
    ResponseEntity?
    - HTTP 응답 전체를 표현하는 객체로, HTTP 상태 코드, 헤더, 응답 본문을 모두 포함할 수 있다.
    - 주로 RESTful 웹 서비스에서 클라이언트에게 명확한 상태 코드와 함께 응답 데이터를 전달하기 위해 사용한다.
    - 정적 메서드(ok(), badRequest(), status(HttpStatus) 등을 사용하여 다양한 상태 코드를 가진 객체를 생성한다.
 */

/*
    @RequestBody?
    - HTTP 요청 본문을 자바 객체로 변환하는 데 사용한다.
    - 클라이언트가 보낸 JSON, XML 등 형식 데이터를 자바 객체로 변환하여 메서드 파라미터(매개변수)로 전달받기 위해 사용한다.
    - HttpMessageConverter 를 사용하여 요청 본문을 자동으로 파싱하고 지정된 자바 객체 타입으로 변환한다.
    - @RequestBody를 사용하려면 메서드의 파라미터가 반드시 바인딩할 객체 타입이어야 하며, 요청 본문은 해당 타입의 데이터를 포함하고 있어야 한다.
 */

@RestController // 해당 컨트롤러가 RESTful 웹 서비스 컨트롤러임을 나타냄
@RequestMapping("/replies") // /replies 경로의 HTTP 요청을 처리
@Log4j2
@RequiredArgsConstructor // 의존성 주입을 위해 설정
public class ReplyController {

    private final ReplyService replyService;

    // 해당 메서드가 Relies POST 작업을 수행하며, POST 방식으로 댓글 등록 기능을 한다
    @Operation(summary = "Replies POST", description = "POST 방식으로 댓글 등록")
    // HTTP Method POST 요청을 처리
    // 요청 경로(URL)은 /replies/ (value = "/")
    // 요청 본문이 JSON 형식임을 나타냄 (consumes)
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    // 클라이언트가 보낸 JSON 데이터를 ReplyDTO 객체로 변환하여 register() 메서드의 매개변수로 받도록 설정
    public Map<String, Long> register(@Valid @RequestBody ReplyDTO replyDTO, BindingResult bindingResult) throws BindException {

        log.info(replyDTO);

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        Map<String, Long> resultMap = new HashMap<>();

        // 추가 코드 부분
        Long rno = replyService.register(replyDTO);

        resultMap.put("rno", rno);

        return resultMap;
    }

    ;

    // 특정 게시물의 댓글 목록 조회 기능
    @Operation(summary = "Replies of Board", description = "GET 방식으로 특정 게시물의 댓글 목록 조회")
    @GetMapping(value = "/list/{bno}") // -- 전체 요청 경로 /replies/list/{bno}
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno, PageRequestDTO pageRequestDTO) {
        // Long bno : 특정 게시물의 번호
        // PageRequestDTO : 페이지 요청 정보
        /*
            @PathVariable?
            URL 경로의 일부를 메서드의 매개변수로 매핑할 때 사용. -- RESTful 웹 서비스에서 URL 경로의 변수를 처리하는 데 주로 사용
            컨트롤러 메서드 매개변수 앞에 일반적으로 사용하며, URL 경로에 있는 변수의 값을 메서드의 매개변수로 전달
            
            현재 예제로 설명
                @PathVariable("bno") Long bno는 URL 경로의 /list/{bno}의 "{bno}" 부분을 메서드 bno 매개변수에 매핑
                예를 들어, /list/100으로 요청이 들어오면, Long bno의 값은 100이 됨

            URL 경로의 변수와 메서드 매개변수 이름이 다른 경우 @PathVariable의 value 속성을 사용하여 매핑할 변수 이름도 설정 가능
         */

        log.info("getList.... 실행");

        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard(bno, pageRequestDTO);

        return responseDTO;
    }

    ;


    // 특정 댓글 조회
    @Operation(summary = "Read Reply", description = "GET 방식으로 특정 댓글 조회")
    @GetMapping("/{rno}")
    public ReplyDTO getReplyDTO(@PathVariable("rno") Long rno) {
        // /{rno} 주소로 요청 넘어온 데이터를 Long rno 매개변수에 할당 (@PathVariable 역할)
        log.info("getReplyDTO 실행.....");
        ReplyDTO replyDTO = replyService.read(rno);

        return replyDTO;
    }

    // 특정 댓글 삭제 -- 일반적으로 REST 방식에서는 DELETE http method를 사용하여 처리
    @Operation(summary = "Delete Reply", description = "DELETE 방식으로 특정 댓글 삭제")
    @DeleteMapping("/{rno}")
    public Map<String, Long> remove(@PathVariable("rno") Long rno) {
        // /{rno} 주소로 요청 넘어온 데이터를 Long rno 매개변수에 할당 (@PathVariable 역할)
        log.info("remove 실행......");
        replyService.remove(rno); // -- 해당 번호를 전달하여 삭제
        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);

        return resultMap;
    }

    // 특정 댓글 수정 -- 일반적으로 REST 방식에서는 PUT http method를 사용하여 처리
    @Operation(summary = "Modify Reply", description = "PUT 방식으로 특정 댓글 수정")
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> modify(@PathVariable("rno") Long rno, @RequestBody ReplyDTO replyDTO) {

        replyDTO.setRno(rno); // 번호 일치
        replyService.modify(replyDTO);
        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno", rno);

        return resultMap;
    }


}

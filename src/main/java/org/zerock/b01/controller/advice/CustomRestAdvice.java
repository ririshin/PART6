package org.zerock.b01.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {

    // BindException이 발생할 때 handleBindException 메서드가 예외 처리를 하도록 설정
    @ExceptionHandler(BindException.class)
    // 해당 메서드가 반환할 HTTP 상태 코드 설정 (EXPECTATION_FAILED -- 417)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String>> handleBindException(BindException e) {

        log.info(e);

        Map<String, String> errorMap = new HashMap<>();

        if (e.hasErrors()) { // -- 예외 e에 오류가 있는지 확인
            // BindingResult : 바인딩 결과와 관련된 오류를 포함하는 객체
            BindingResult bindingResult = e.getBindingResult();
            // 모든 필드 오류를 반복 (getFieldErrors : FieldError 객체들의 리스트를 반환)
            bindingResult.getFieldErrors().forEach(fieldError -> {
                // 각 필드에 대한 필드 이름과 오류 코드를 errorMap에 추가
                errorMap.put(fieldError.getField(), fieldError.getCode());
            });
        }

        // HTTP 상태 코드 400(Bad Request)와 함께 errorMap을 응답 본문으로 반환
        // -- 클라이언트에게 잘못된 요청임을 알림
        return ResponseEntity.badRequest().body(errorMap);
    }

    /*
        DataIntegrityViolationException?
        데이터베이스의 무결성 제약 조건을 위반했을 때 발생하는 예외

        [주요 발생 상황]
        1. Unique Constraint Violation (고유 제약 조건 위반)
            데이터베이스 테이블에서 특정 컬럼에 고유 제약 조건이 설정되어 있는데, 해당 컬럼에 중복 값을 삽입하려고 하는 경우
        2. Foreign Key Constraint Violation (외래 키 제약 조건 위반)
            데이터베이스에서 외래 키 제약 조건이 설정되어 있는 컬럼에 유효하지 않은 값을 삽입하려고 하는 경우
        3. Not Null Constraint Violation
            NOT NULL 제약 조건이 설정된 컬럼에 null 값을 삽입하려고 하는 경우
        4. Check Constraint Violation
            CHECK 제약 조건이 설정된 컬럼에 유효하지 않은 값을 삽입하려고 하는 경우
            예) 나이 컬럼에 음수 값을 삽입하려고 할 때.
        5. Primary Key Constraint Violation
            기본 키 제약 조건을 위반하는 중복된 기본 키 값을 삽입하려고 하는 경우

     */

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String>> handleFKException(Exception e) {
        log.error(e);

        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("time", ""+System.currentTimeMillis());
        errorMap.put("msg", "constraint fails");

        return ResponseEntity.badRequest().body(errorMap);
    }

    /*
        NoSuchElementException?
        특정 요소를 찾지 못할 때 발생하는 예외 -- 컬렉션이나 반복자에서 요소를 검색하거나 접근하려 할 때 해당 요소가 존재하지 않으면 발생
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String>> handleNoSuchException(Exception e) {
        log.error(e);

        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("time", ""+System.currentTimeMillis());
        errorMap.put("msg", "No Such Element Exception");
        return ResponseEntity.badRequest().body(errorMap);
    }

}

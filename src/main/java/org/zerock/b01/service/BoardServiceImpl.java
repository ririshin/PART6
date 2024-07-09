package org.zerock.b01.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.BoardDTO;
import org.zerock.b01.dto.BoardListReplyCountDTO;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.repository.BoardRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
    @Transactional
        트랜잭션 경계를 지정하여 메서드 실행 시 트랜잭션을 시작 및 메서드 종료 시 트랜잭션을 커밋 or 롤백
        -- 이를 통해 데이터베이스 작업의 원자성을 보장, 일관된 상태를 유지
 */

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional // -- 해당 객체를 감싸는 별도의 클래스를 생성
public class BoardServiceImpl implements BoardService{

    private final ModelMapper modelMapper;
    private final BoardRepository boardRepository;

    @Override
    public Long register(BoardDTO boardDTO) {
        Board board = modelMapper.map(boardDTO, Board.class);
        Long bno = boardRepository.save(board).getBno();
        return bno;
    }

    @Override
    public BoardDTO readOne(Long bno) {
        // findByID() -- 반환 타입 Optional 타입이라는 것
        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow(); // -- Optional이 비어있는 경우 예외 던짐
        return modelMapper.map(board, BoardDTO.class); // -- 엔티티를 DTO로 변환
    }

    @Override
    public void modify(BoardDTO boardDTO) {
        Optional<Board> result = boardRepository.findById(boardDTO.getBno());
        Board board = result.orElseThrow();
        board.change(boardDTO.getTitle(), boardDTO.getContent());
        boardRepository.save(board);
    }

    @Override
    public void remove(Long bno) {
        boardRepository.deleteById(bno);
    }

    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());

        return PageResponseDTO.<BoardDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {
        // PageRequestDTO로부터 types(검색 타입), keyword(검색어) 추출
        String[] types = pageRequestDTO.getTypes();
        String keyword = pageRequestDTO.getKeyword();

        // PageRequestDTO로부터 Pageable 객체 생성 (정렬 기준 : bno)
        Pageable pageable = pageRequestDTO.getPageable("bno");

        // boardRepository의 searchWithReplyCount 메서드 호출하여 검색 결과 가져옴
        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        // PageResponseDTO 객체 생성하여 반환
        return PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO) // -- 페이지 요청 정보
                .dtoList(result.getContent()) // -- 검색 결과 목록
                .total((int)result.getTotalElements()) // -- 전체 검색 결과 수
                .build();
    }

}

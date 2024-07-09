package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.Reply;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.repository.ReplyRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReplyServiceImpl implements ReplyService {

    private final ReplyRepository replyRepository;
    private final ModelMapper modelMapper;

    // Reply 엔티티 객체가 Board 엔티티 객체를 참조하기 때문에 별도 처리 필요
    // ReplyDTO를 Reply 엔티티로 변환할 때 bno 값을 포함할 수 있도록 별도 처리하는 method 선언
    private Reply dtoToEntity(ReplyDTO replyDTO) {
        // Board 객체 생성하여 설정
        Board board = Board.builder().bno(replyDTO.getBno()).build();

        // Reply 객체를 생성하여 반환
        return Reply.builder()
                .board(board)
                .replyText(replyDTO.getReplyText())
                .replyer(replyDTO.getReplyer())
                .build();
    }

    // Reply 엔티티를 ReplyDTO로 변환할 때 bno 값을 포함할 수 있도록 별도 처리하는 method 선언
    private ReplyDTO entityToDto(Reply reply) {
        return ReplyDTO.builder()
                .rno(reply.getRno())
                .replyText(reply.getReplyText())
                .replyer(reply.getReplyer())
                .regDate(reply.getRegDate())
                .modDate(reply.getModDate())
                .bno(reply.getBoard().getBno())
                .build();
    }

    @Override
    public Long register(ReplyDTO replyDTO) {
        // Reply reply = modelMapper.map(replyDTO, Reply.class);
        Reply reply = dtoToEntity(replyDTO);
        return replyRepository.save(reply).getRno();
    }

    @Override
    public ReplyDTO read(Long rno) {
        Optional<Reply> replyOptional = replyRepository.findById(rno);
        Reply reply = replyOptional.orElseThrow();

        // return modelMapper.map(reply, ReplyDTO.class);

        return entityToDto(reply);
    }

    @Override
    public void modify(ReplyDTO replyDTO) { // -- 매개변수 replyDTO (수정 내용이 담긴 replyDTO)
        Optional<Reply> replyOptional = replyRepository.findById(replyDTO.getRno());
        Reply reply = replyOptional.orElseThrow();
        reply.changeText(replyDTO.getReplyText()); // changeText() -- 댓글 내용만 수정하는 메서드
        replyRepository.save(reply);
    }

    @Override
    public void remove(Long rno) {

        // 없는 번호를 조회하여 삭제하려고 하면 예외 발생할 수 있도록 설정
        Optional<Reply> replyOptional = replyRepository.findById(rno);
        if(replyOptional.isEmpty()) {
            throw new NoSuchElementException(); // -- 해당 요소를 찾지 못했다는 예외 발생
        }

        // 만약 해당 번호를 문제 없이 찾았다면 삭제 진행
        replyRepository.deleteById(rno);
    }

    // 특정 게시글(bno)에 대한 "댓글 목록" 페이지네이션하여 반환
    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {
        // bno -- 댓글이 달린 특정 게시글 번호
        // PageRequestDTO -- 댓글 목록 페이지네이션 요청 정보

        // pageRequestDTO를 이용하여 페이지 번호, 페이지 크기(한 페이지에 조회할 데이터 수), 정렬 기준을 생성
        // 페이지 번호가 0 이하인 경우 0으로 설정(1 페이지가 됨), 그 외에는 페이지 번호에서 1을 뺀 값을 사용
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() <= 0 ? 0 : pageRequestDTO.getPage() - 1
                , pageRequestDTO.getSize()
                , Sort.by("rno").ascending()
        );

        // 특정 게시글에 대한 댓글 목록 조회
        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        // Page<Reply> 객체 : 조회해 온 데이터 뿐만 아니라 페이지의 정보를 가지고 있는 객체
        // Page<Reply> 객체의 내용 중 조회한 댓글 목록을 가지고 reply 객체를 dto 타입으로 변환 및 리스트로 수집하는 스트림 실행
        List<ReplyDTO> dtoList = result.getContent().stream().map(reply -> modelMapper.map(reply, ReplyDTO.class)).toList();

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements()) // -- 전체 댓글 수
                .build();
    }
}

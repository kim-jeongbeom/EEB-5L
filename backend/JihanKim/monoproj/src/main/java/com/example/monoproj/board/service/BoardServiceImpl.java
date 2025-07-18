package com.example.monoproj.board.service;

import com.example.monoproj.account.entity.Account;
import com.example.monoproj.account.repository.AccountRepository;
import com.example.monoproj.account_profile.entity.AccountProfile;
import com.example.monoproj.account_profile.repository.AccountProfileRepository;
import com.example.monoproj.board.entity.Board;
import com.example.monoproj.board.repository.BoardRepository;
import com.example.monoproj.board.service.request.CreateBoardRequest;
import com.example.monoproj.board.service.request.ListBoardRequest;
import com.example.monoproj.board.service.response.CreateBoardResponse;
import com.example.monoproj.board.service.response.ListBoardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    final private BoardRepository boardRepository;
    final private AccountRepository accountRepository;
    final private AccountProfileRepository accountProfileRepository;

    @Override
    public ListBoardResponse list(ListBoardRequest request) {
        // PageRequest 생성 (0부터 시작하는 페이지 인덱스를 위해 page - 1을 사용)
        PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getPerPage());

        // 페이징된 게시글 목록을 가져오기
        Page<Board> boardPage = boardRepository.findAllWithWriter(pageRequest);

        // ListBoardResponse 객체로 변환하여 반환
        return new ListBoardResponse(boardPage.getContent(), boardPage.getTotalElements(), boardPage.getTotalPages());
    }

    @Override
    public CreateBoardResponse register(CreateBoardRequest createBoardRequest) {
        log.info("accountId: {}", createBoardRequest.getAccountId());

        Account account = accountRepository.findById(createBoardRequest.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account 존재하지 않음"));

        log.info("account: {}", account);

        AccountProfile accountProfile = accountProfileRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("AccountProfile not found"));

        log.info("account profile: {}", accountProfile);

        Board board = boardRepository.save(createBoardRequest.toBoard(accountProfile));
        return CreateBoardResponse.from(board);
    }
}

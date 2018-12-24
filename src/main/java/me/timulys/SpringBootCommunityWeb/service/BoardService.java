package me.timulys.SpringBootCommunityWeb.service;

import me.timulys.SpringBootCommunityWeb.domain.Board;
import me.timulys.SpringBootCommunityWeb.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Page<Board> findBoardList(Pageable pageable) {
        pageable = new PageRequest(pageable.getPageNumber() <= 0 ?
                0 : pageable.getPageNumber() - 1, pageable.getPageSize());
        return boardRepository.findAll(pageable);
    }

    public Board findBoardByIdx(Long idx) {
        return boardRepository.getOne(idx);/*FIXME : 왜 안되는지 확인해볼 것.orElse(new Board());*/
    }
}

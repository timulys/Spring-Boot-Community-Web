package me.timulys.SpringBootCommunityWeb.repository;

import me.timulys.SpringBootCommunityWeb.domain.Board;
import me.timulys.SpringBootCommunityWeb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByUser(User user);
}

package me.timulys.SpringBootCommunityWeb.repository;

import me.timulys.SpringBootCommunityWeb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}

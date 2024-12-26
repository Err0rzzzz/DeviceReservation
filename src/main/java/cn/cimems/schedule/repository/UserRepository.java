package cn.cimems.schedule.repository;

import cn.cimems.schedule.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // 根据用户名查找用户
    Optional<User> findByEmail(String email);       // 根据邮箱查找用户
}

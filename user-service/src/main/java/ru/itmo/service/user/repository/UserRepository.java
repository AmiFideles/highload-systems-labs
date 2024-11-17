package ru.itmo.service.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import ru.itmo.service.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Modifying
    Integer removeById(Long id);
    boolean existsByName(String username);
}

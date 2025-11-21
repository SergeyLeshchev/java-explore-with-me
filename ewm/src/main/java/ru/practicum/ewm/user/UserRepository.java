package ru.practicum.ewm.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    List<User> findAllByIdIn(List<Long> ids);

    @Query(value = "SELECT * FROM users ORDER BY id LIMIT :size OFFSET :from",
            nativeQuery = true)
    List<User> findAllFromAndSize(@Param("from") Integer from,
                                  @Param("size") Integer size);
}

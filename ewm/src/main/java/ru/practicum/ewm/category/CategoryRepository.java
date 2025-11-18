package ru.practicum.ewm.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    Category findByName(String name);

    @Query(value = "SELECT * FROM categories ORDER BY id LIMIT :size OFFSET :from",
            nativeQuery = true)
    List<Category> findAllFromAndSize(@Param("from") int from,
                                      @Param("size") int size);
}

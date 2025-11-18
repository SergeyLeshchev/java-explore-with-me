package ru.practicum.ewm.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.compilation.model.Compilation;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query(value = "SELECT * FROM compilations ORDER BY id LIMIT :size OFFSET :from",
            nativeQuery = true)
    List<Compilation> findAllFromAndSize(@Param("from") Integer from,
                                         @Param("size") Integer size);

    @Query(value = "SELECT * FROM compilations WHERE pinned = :pinned ORDER BY id LIMIT :size OFFSET :from",
            nativeQuery = true)
    List<Compilation> findAllFromAndSizeAndByPinned(@Param("pinned") Boolean pinned,
                                                    @Param("from") Integer from,
                                                    @Param("size") Integer size);
}

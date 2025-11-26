package ru.practicum.ewm.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.CommentCountProjection;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventId(Long eventId);

    Integer countByEventId(Long eventId);

    @Query("SELECT c.event.id as eventId, COUNT(c) as commentCount " +
            "FROM Comment c WHERE c.event.id IN :eventIds GROUP BY c.event.id")
    List<CommentCountProjection> getCommentCountsByEventIds(@Param("eventIds") List<Long> eventIds);
}

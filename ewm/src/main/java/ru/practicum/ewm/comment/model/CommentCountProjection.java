package ru.practicum.ewm.comment.model;

public interface CommentCountProjection {
    Long getEventId();

    Integer getCommentCount();
}

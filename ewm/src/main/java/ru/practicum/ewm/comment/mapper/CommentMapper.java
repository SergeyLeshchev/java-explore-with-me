package ru.practicum.ewm.comment.mapper;

import ru.practicum.ewm.DateTimeMapper;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.model.Comment;

public class CommentMapper {
    public static Comment mapToComment(CommentRequestDto dto) {
        return new Comment();
    }

    public static CommentResponseDto mapToCommentResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getEvent().getTitle(),
                comment.getAuthor().getName(),
                DateTimeMapper.mapToString(comment.getCreatedOn())
        );
    }
}

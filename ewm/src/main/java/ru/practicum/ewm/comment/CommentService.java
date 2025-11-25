package ru.practicum.ewm.comment;

import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.CommentUpdateRequest;

import java.util.List;

public interface CommentService {
    CommentResponseDto addComment(CommentRequestDto newComment);

    CommentResponseDto updateComment(CommentUpdateRequest newComment);

    CommentResponseDto getComment(Long commentId);

    List<CommentResponseDto> getComments();

    List<CommentResponseDto> getCommentsByEventId(Long eventId);

    void deleteCommentUser(Long userId, Long commentId);

    void deleteCommentAdmin(Long commentId);
}

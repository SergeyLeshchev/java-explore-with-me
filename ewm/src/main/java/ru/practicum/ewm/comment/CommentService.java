package ru.practicum.ewm.comment;

import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.CommentUpdateRequest;

import java.util.List;

public interface CommentService {
    CommentResponseDto addComment(Long userId, Long eventId, CommentRequestDto newComment);

    CommentResponseDto updateComment(Long userId, Long commentId, CommentUpdateRequest newComment);

    CommentResponseDto getComment(Long commentId);

    List<CommentResponseDto> getComments();

    void deleteCommentUser(Long userId, Long commentId);

    void deleteCommentAdmin(Long commentId);
}

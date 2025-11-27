package ru.practicum.ewm.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.CommentUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/users/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto addComment(@RequestBody @Valid CommentRequestDto newComment) {
        return commentService.addComment(newComment);
    }

    @PatchMapping("/users/comments")
    public CommentResponseDto updateComment(@RequestBody @Valid CommentUpdateRequest newComment) {
        return commentService.updateComment(newComment);
    }

    @GetMapping("/comments/{commentId}")
    public CommentResponseDto getComment(@PathVariable Long commentId) {
        return commentService.getComment(commentId);
    }

    @GetMapping("/comments")
    public List<CommentResponseDto> getComments() {
        return commentService.getComments();
    }

    @GetMapping("/comments/events/{eventId}")
    public List<CommentResponseDto> getCommentsByEventId(@PathVariable Long eventId) {
        return commentService.getCommentsByEventId(eventId);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentUser(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.deleteCommentUser(userId, commentId);
    }

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentAdmin(commentId);
    }
}

package ru.practicum.ewm.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.CommentUpdateRequest;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.DataAccessException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public CommentResponseDto addComment(CommentRequestDto newComment) {
        User user = userRepository.findById(newComment.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + newComment.getUserId() + " не найден."));
        Event event = eventRepository.findById(newComment.getEventId())
                .orElseThrow(() -> new NotFoundException("Событие с id " + newComment.getEventId() + " не найдено."));
        if (!Objects.equals(event.getState(), State.PUBLISHED)) {
            throw new ConflictException("Событие должно быть опубликовано");
        }
        Comment comment = new Comment(
                null,
                newComment.getText(),
                event,
                user,
                ZonedDateTime.now(ZoneOffset.UTC)
        );
        return CommentMapper.mapToCommentResponseDto(commentRepository.save(comment));
    }

    public CommentResponseDto updateComment(CommentUpdateRequest newComment) {
        User user = userRepository.findById(newComment.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + newComment.getUserId() + " не найден."));
        Comment comment = commentRepository.findById(newComment.getCommentId())
                .orElseThrow(() -> new NotFoundException("Комментарий с id " + newComment.getCommentId() + " не найден."));
        if (!Objects.equals(user.getId(), comment.getAuthor().getId())) {
            throw new DataAccessException("Только автор комментария может менять комментарий");
        }
        comment.setText(newComment.getText());
        return CommentMapper.mapToCommentResponseDto(commentRepository.save(comment));
    }

    public CommentResponseDto getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id " + commentId + " не найден."));
        return CommentMapper.mapToCommentResponseDto(comment);
    }

    public List<CommentResponseDto> getComments() {
        return commentRepository.findAll().stream()
                .map(CommentMapper::mapToCommentResponseDto)
                .toList();
    }

    public List<CommentResponseDto> getCommentsByEventId(Long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено."));
        return commentRepository.findAllByEventId(eventId).stream()
                .map(CommentMapper::mapToCommentResponseDto)
                .toList();
    }

    public void deleteCommentUser(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id " + commentId + " не найден."));
        if (!Objects.equals(user.getId(), comment.getAuthor().getId())) {
            throw new DataAccessException("Только автор комментария может удалять комментарий");
        }
        commentRepository.deleteById(commentId);
    }

    public void deleteCommentAdmin(Long commentId) {
        commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id " + commentId + " не найден."));
        commentRepository.deleteById(commentId);
    }
}

package ro.code4.expertconsultation.comment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.code4.expertconsultation.comment.mapper.CommentMapper;
import ro.code4.expertconsultation.comment.model.CommentCreationRequest;
import ro.code4.expertconsultation.comment.model.dto.CommentDto;
import ro.code4.expertconsultation.comment.model.persistence.Comment;
import ro.code4.expertconsultation.comment.repository.CommentRepository;
import ro.code4.expertconsultation.comment.service.impl.CommentServiceImpl;
import ro.code4.expertconsultation.core.exception.ExpertConsultationException;
import ro.code4.expertconsultation.document.model.persistence.DocumentBlock;
import ro.code4.expertconsultation.document.service.DocumentBlockService;
import ro.code4.expertconsultation.user.model.persistence.User;
import ro.code4.expertconsultation.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ro.code4.expertconsultation.utils.FactoryManager.commentFactory;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    private CommentDto commentDto;

    @Mock
    private DocumentBlockService documentBlockService;

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl sut;

    @BeforeEach
    void setUp() {
        commentDto = commentFactory.getCommentDto();
    }

    @Test
    void given_existing_ids_when_get_then_return_dto() {
        //given
        long id = 1;

        Comment commentMock = mock(Comment.class);
        when(commentRepository.findById(id)).thenReturn(Optional.of(commentMock));
        when(commentMapper.map(commentMock)).thenReturn(commentDto);

        // when
        CommentDto result = sut.get(id);

        //then
        assertThat(result).isEqualTo(commentDto);
    }

    @Test
    void given_nonexistent_commentId_when_get_then_throw_exception() {
        //given
        long id = 2; //nonexistent

        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        //then
        assertThrows(EntityNotFoundException.class, () -> sut.get(id));
    }

    @Test
    void given_any_null_value_when_get_then_throw_exception() {

        assertThrows(ExpertConsultationException.class, () -> sut.get(null));
    }

    @Test
    void given_valid_commentRequest_when_create_then_comment_is_created() {
        //given
        long userId = 1;
        long documentId = 1;
        long blockId = 1;

        User authorMock = mock(User.class);
        when(userService.getEntity(userId)).thenReturn(authorMock);

        DocumentBlock documentBlockMock = mock(DocumentBlock.class);
        when(documentBlockService.getEntity(blockId)).thenReturn(documentBlockMock);

        CommentDto commentDtoMock = mock(CommentDto.class);
        Comment commentMock = mock(Comment.class);
        when(commentMapper.map(commentDtoMock)).thenReturn(commentMock);

        CommentCreationRequest creationRequest = new CommentCreationRequest(userId, documentId, blockId, commentDtoMock);

        Comment savedCommentMock = mock(Comment.class);
        when(commentRepository.save(commentMock)).thenReturn(savedCommentMock);
        when(commentMapper.map(savedCommentMock)).thenReturn(commentDto);

        //when
        CommentDto result = sut.create(creationRequest);

        //then
        verify(commentRepository).save(commentMock);
        assertThat(result).isSameAs(commentDto);

    }

    @Test
    void given_null_value_when_create_then_throw_exception() {

        //given
        long userId = 1;
        long documentId = 1;
        long blockId = 1;

        CommentCreationRequest creationRequest = new CommentCreationRequest(userId, documentId, blockId, null);

        //then
        assertThrows(ExpertConsultationException.class, () -> sut.create(creationRequest));
    }

    @Test
    void given_any_null_values_when_create_then_throw_exception() {

        assertThrows(ExpertConsultationException.class, () -> sut.create(null));
    }

    @Test
    void given_commentDto_and_valid_id_when_update_then_return_updated_dto() {
        //given
        long commentId = 1;
        CommentDto update = this.commentDto;
        update.setContent("Updated content");
        Comment commentMock = mock(Comment.class);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentMock));
        when(commentMapper.map(commentMock)).thenReturn(update);

        //when
        CommentDto result = sut.update(commentId, update);

        //then
        assertThat(result).isEqualTo(update);
        verify(commentRepository).save(commentMock);
    }

    @Test
    void given_commentDto_and_invalid_id_when_update_then_throw_exception() {
        //given
        long commentId = 2;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        //then
        assertThrows(EntityNotFoundException.class, () -> sut.update(commentId, commentDto));

    }

    @Test
    void given_any_null_value_when_update_then_throw_exception() {

        assertThrows(ExpertConsultationException.class, () -> sut.update(null, commentDto));
    }

    @Test
    void given_any_null_value_when_delete_then_throw_exception() {

        assertThrows(ExpertConsultationException.class, () -> sut.delete(null));
    }

    @Test
    void given_valid_block_id_when_list_then_return_dto_list() {
        //give
        long documentBlockId = 1L;
        Comment commentMock = mock(Comment.class);
        when(commentRepository.findCommentsByDocumentBlock(documentBlockId)).thenReturn(List.of(commentMock));
        when(commentMapper.map(commentMock)).thenReturn(commentDto);

        //when
        List<CommentDto> results = sut.list(documentBlockId);

        //then
        assertThat(results.get(0)).isEqualTo(commentDto);
    }

    @Test
    void given_invalid_block_id_when_list_then_return_empty_list() {
        //give
        long documentBlockId = 2L; //invalid

        when(commentRepository.findCommentsByDocumentBlock(documentBlockId)).thenReturn(Collections.EMPTY_LIST);

        //when
        List<CommentDto> results = sut.list(documentBlockId);

        //then
        assertThat(results.size()).isZero();
    }

    @Test
    void given_null_when_list_then_throw_exception() {

        assertThrows(ExpertConsultationException.class, () -> sut.list(null));
    }
}


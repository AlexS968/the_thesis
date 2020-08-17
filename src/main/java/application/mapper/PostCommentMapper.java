package application.mapper;

import application.exception.ApiValidationException;
import application.exception.BadRequestException;
import application.exception.apierror.ApiValidationError;
import application.model.Post;
import application.model.PostComment;
import application.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PostCommentMapper {

    public PostComment convertToEntity(User user, Post post, PostComment parentComment,
                                       Long parentCommentId, String text) throws ApiValidationException {
        if (text.length() < 30) {
            throw new ApiValidationException(
                    new ApiValidationError("Текст комментария не задан или слишком короткий"), "");
        }
        if (post == null || (parentCommentId != null & parentComment == null)) {
            throw new BadRequestException();
        }
        return new PostComment(parentComment, post, user, LocalDateTime.now(), text);
    }
}

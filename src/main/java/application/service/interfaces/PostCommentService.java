package application.service.interfaces;

import application.api.request.PostCommentRequest;
import application.api.response.CommentResponse;
import application.model.PostComment;

import java.security.Principal;
import java.util.List;

public interface PostCommentService {

    List<PostComment> getTagsToPost(long id);

    PostComment getCommentById(long id);

    CommentResponse addPostComment(PostCommentRequest request, Principal principal);
}

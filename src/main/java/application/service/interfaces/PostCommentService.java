package application.service.interfaces;

import application.api.request.PostCommentRequest;
import application.model.PostComment;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface PostCommentService {

    List<PostComment> getTagsToPost(long id);

    long addPostComment(PostCommentRequest request, HttpSession session);
}

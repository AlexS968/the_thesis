package application.service.interfaces;

import application.model.PostComment;

import java.util.List;

public interface PostCommentService {

    List<PostComment> getTagsToPost(long id);

    long addPostComment(PostComment comment);
}

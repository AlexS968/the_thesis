package application.service;

import application.model.PostComment;

import java.util.List;

public interface PostCommentService {

    List<PostComment> getTagsToPost(long id);
}

package application.service;

import application.persistence.model.TagToPost;

import java.util.Optional;

public interface TagToPostService {

    Optional<TagToPost> findByTagIdAndByPostId(long tagId, long postId);

    void deleteAllByPostId(long postId);
}

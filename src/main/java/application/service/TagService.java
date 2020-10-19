package application.service;

import application.persistence.model.Post;
import application.persistence.model.Tag;
import application.persistence.model.TagToPost;
import application.persistence.repository.IPostCount;

import java.util.List;

public interface TagService {

    List<Tag> getTags(String query);

    List<String> getTagsToPost(long id);

    Tag saveTag(Tag tag);

    TagToPost getOrSaveTag(String name, Post post);

    void deleteAllTagsToPost(Post post);

    void deleteTag(long id);
}

package application.service.interfaces;

import application.model.Post;
import application.model.Tag;
import application.model.TagToPost;
import application.model.repository.IPostCount;

import java.util.List;

public interface TagService {

    List<Tag> getTags(String query);

    List<String> getTagsToPost(long id);

    Tag saveTag(Tag tag);

    TagToPost getOrSaveTag(String name, Post post);

    void deleteAllTagsToPost(Post post);

    void deleteTag(long id);

    List<IPostCount> getWeights();
}

package application.service;

import application.model.Post;
import application.model.Tag;

import java.util.List;

public interface TagService {

    List<Tag> getTags(String query);

    List<String> getTagsToPost(long id);

    Tag saveTag(Tag tag);

    void deleteTag(long id);
}

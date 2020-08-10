package application.service.interfaces;

import application.model.Tag;
import application.repository.IPostCount;

import java.util.List;

public interface TagService {

    List<Tag> getTags(String query);

    List<String> getTagsToPost(long id);

    Tag saveTag(Tag tag);

    void deleteTag(long id);

    List<IPostCount> getWeights();
}

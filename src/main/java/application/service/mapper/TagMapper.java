package application.service.mapper;

import application.api.response.TagsResponse;
import application.api.response.type.TagResponse;
import application.persistence.model.Tag;
import application.persistence.repository.IPostCount;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagMapper {

    public TagsResponse convertToDto(List<Tag> tags, List<IPostCount> weights) {
        Integer max = weights.stream().map(IPostCount::getTotal).reduce(Integer::max).orElse(null);
        List<TagResponse> res = new ArrayList<>();
        for (Tag tag : tags) {
            for (IPostCount element : weights) {
                if (tag.getName().equals(element.getName())) {
                    res.add(new TagResponse(
                            element.getName(),
                            (double) element.getTotal() / (double) max));
                }
            }
        }
        return new TagsResponse(res.toArray(new TagResponse[0]));
    }
}

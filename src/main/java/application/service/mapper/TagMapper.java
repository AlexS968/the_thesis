package application.service.mapper;

import application.api.response.type.TagResponse;
import application.api.response.TagsResponse;
import application.model.Tag;
import application.model.repository.IPostCount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagMapper {

    public TagsResponse convertToDto(List<Tag> tags, List<IPostCount> weights) {
        Integer max = weights.stream().map(IPostCount::getTotal).reduce(Integer::max).orElse(null);
        TagResponse[] res = new TagResponse[tags.size()];
        int i = 0;
        for (Tag tag : tags) {
            for (IPostCount element : weights) {
                if (tag.getName().equals(element.getName())) {
                    res[i] = new TagResponse(element.getName(),
                            (double) element.getTotal() / (double) max);
                }
            }
            i++;
        }
        return new TagsResponse(res);
    }
}

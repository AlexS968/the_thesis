package application.mapper;

import application.api.response.TagsResponse;
import application.model.Tag;
import application.repository.IPostCount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagMapper {

    public TagsResponse convertToDto(List<Tag> tags, List<IPostCount> weights) {
        Integer max = weights.stream().map(IPostCount::getTotal).reduce(Integer::max).orElse(null);
        TagsResponse.TagResponse[] res = new TagsResponse.TagResponse[tags.size()];
        int i = 0;
        for (Tag tag : tags) {
            for (IPostCount element : weights) {
                if (tag.getName().equals(element.getName())) {
                    res[i] = new TagsResponse.TagResponse(element.getName(),
                            (double) element.getTotal() / (double) max);
                }
            }
            i++;
        }
        return new TagsResponse(res);
    }
}

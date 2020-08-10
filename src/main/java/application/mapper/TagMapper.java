package application.mapper;

import application.api.response.TagResponse;
import application.model.Tag;
import application.repository.IPostCount;
import application.service.TagServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagMapper {

    private final TagServiceImpl tagService;

    public TagMapper(TagServiceImpl tagService) {
        this.tagService = tagService;
    }

    public TagResponse[] convertToDto(List<Tag> tags) {
        List<IPostCount> weights = tagService.getWeights();
        Integer max = weights.stream().map(IPostCount::getTotal).reduce(Integer::max).orElse(null);
        TagResponse[] responses = new TagResponse[tags.size()];
        int i = 0;
        for (Tag tag : tags) {
            for (IPostCount element : weights) {
                if (tag.getName().equals(element.getName())) {
                    responses[i] = new TagResponse(element.getName(),
                            (double) element.getTotal() / (double) max);
                    i++;
                }
            }
        }
        return responses;
    }
}

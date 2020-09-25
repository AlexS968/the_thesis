package application.api.response;

import application.api.response.type.TagResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagsResponse {
    private TagResponse[] tags;
}


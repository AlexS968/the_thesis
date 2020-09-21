package application.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagsResponse {
    private TagResponse[] tags;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagResponse {
        private String name;
        private double weight;
    }
}


package application.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private long timestamp;
    private int active;
    private String title;
    private String[] tags;
    private String text;

    public String[] getTags() {
        return Arrays.stream(tags).map(String::toUpperCase).toArray(String[]::new);
    }
}

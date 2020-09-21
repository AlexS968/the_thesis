package application.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private long timestamp;
    private int active;
    private String title;
    private String[] tags;
    private String text;
}

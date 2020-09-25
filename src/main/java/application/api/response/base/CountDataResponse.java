package application.api.response.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class CountDataResponse extends PostDataResponse{
    private long likeCount;
    private long dislikeCount;
    private int viewCount;
}

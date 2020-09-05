package application.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LikeRequest {

    @JsonProperty("post_id")
    private long postId;

    public LikeRequest() {
    }

    public LikeRequest(long postId) {
        this.postId = postId;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }
}

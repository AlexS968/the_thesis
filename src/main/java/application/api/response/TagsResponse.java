package application.api.response;

public class TagsResponse {

    private TagResponse[] tags;

    public TagsResponse(TagResponse[] tags) {
        this.tags = tags;
    }

    public TagResponse[] getTags() {
        return tags;
    }

    public void setTags(TagResponse[] tags) {
        this.tags = tags;
    }
}


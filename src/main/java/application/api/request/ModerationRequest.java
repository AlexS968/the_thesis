package application.api.request;

public class ModerationRequest {

    private long post_id;
    private String decision; //решение по посту: accept или decline

    public ModerationRequest() {
    }

    public ModerationRequest(long post_id, String decision) {
        this.post_id = post_id;
        this.decision = decision;
    }

    public long getPost_id() {
        return post_id;
    }

    public void setPost_id(long post_id) {
        this.post_id = post_id;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}

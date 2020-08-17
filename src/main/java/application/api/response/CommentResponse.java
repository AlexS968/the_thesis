package application.api.response;

public class CommentResponse {

    long id;

    public CommentResponse() {
    }

    public CommentResponse(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}


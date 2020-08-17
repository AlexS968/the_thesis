package application.api.response;

public class PostCommentResponse {

    private long id;
    private long timestamp;
    //Текст поста в формате HTML
    private String text;
    private UserPostCommentResponse user;

    public PostCommentResponse() {
    }

    public PostCommentResponse(long id, long timestamp, String text, UserPostCommentResponse user) {
        this.id = id;
        this.timestamp = timestamp;
        this.text = text;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UserPostCommentResponse getUser() {
        return user;
    }

    public void setUser(UserPostCommentResponse user) {
        this.user = user;
    }

    public static class UserPostCommentResponse {


        private long id;
        private String name;
        private String photo;

        public UserPostCommentResponse() {
        }

        public UserPostCommentResponse(long id, String name, String photo) {
            this.id = id;
            this.name = name;
            this.photo = photo;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }
    }
}

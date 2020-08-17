package application.api.response;

public class AuthenticationResponse {

    private Boolean result;
    private UserAuthCheckResponse user;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public UserAuthCheckResponse getUser() {
        return user;
    }

    public void setUser() {
        this.user = new UserAuthCheckResponse();
    }

    public void setUserId(long id) {
        this.user.setId(id);
    }

    public void setUserName(String name) {
        this.user.setName(name);
    }

    public void setUserPhoto(String photo) {
        this.user.setPhoto(photo);
    }

    public void setUserEmail(String email) {
        this.user.setEmail(email);
    }

    public void setUserModeration(boolean moderation) {
        this.user.setModeration(moderation);
    }

    public void setUserModerationCount(int moderationCount) {
        this.user.setModerationCount(moderationCount);
    }

    public void setUserSettings(boolean settings) {
        this.user.setSettings(settings);
    }

    static class UserAuthCheckResponse {

        private long id;
        private String name;
        private String photo;
        private String email;
        private boolean moderation;
        private int moderationCount;
        private boolean settings;

        public void setId(long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setModeration(boolean moderation) {
            this.moderation = moderation;
        }

        public void setModerationCount(int moderationCount) {
            this.moderationCount = moderationCount;
        }

        public void setSettings(boolean settings) {
            this.settings = settings;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPhoto() {
            return photo;
        }

        public String getEmail() {
            return email;
        }

        public boolean isModeration() {
            return moderation;
        }

        public int getModerationCount() {
            return moderationCount;
        }

        public boolean isSettings() {
            return settings;
        }
    }
}


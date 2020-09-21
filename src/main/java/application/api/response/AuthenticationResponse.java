package application.api.response;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class AuthenticationResponse {

    private Boolean result;
    private UserAuthCheckResponse user;

    public void setResult(Boolean result) {
        this.result = result;
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

    @Data
    @NoArgsConstructor
    static class UserAuthCheckResponse {
        private long id;
        private String name;
        private String photo;
        private String email;
        private boolean moderation;
        private int moderationCount;
        private boolean settings;
    }
}


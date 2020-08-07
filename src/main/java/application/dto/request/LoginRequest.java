package application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {

      private String email;
      private String password;

    @JsonProperty("e_mail")
    public String getEmail() {
        return email;
    }

    public void setEmail(String eMail) {
        this.email = eMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

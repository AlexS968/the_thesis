package application.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //date and time of captcha generation
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime time;

    //code displayed in the captcha image
    @Column(length = 16, nullable = false)
    private String code;

    //secret captcha code
    @NotBlank
    @Column(name = "secret_code", length = 16, nullable = false)
    private String secretCode;

    public CaptchaCode() {
    }

    public CaptchaCode(LocalDateTime time, String code, @NotBlank String secretCode) {
        this.time = time;
        this.code = code;
        this.secretCode = secretCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    @Override
    public String toString() {
        return "CaptchaCode{" +
                "id=" + id +
                ", time=" + time +
                ", code='" + code + '\'' +
                ", secretCode='" + secretCode + '\'' +
                '}';
    }
}

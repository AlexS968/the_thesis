package application.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime time; //date and time of captcha generation
    @Column(length = 16, nullable = false)
    private String code; //code displayed in the captcha image
    @NotBlank
    @Column(name = "secret_code", length = 16, nullable = false)
    private String secretCode; //secret captcha code

    public CaptchaCode(LocalDateTime time, String code, @NotBlank String secretCode) {
        this.time = time;
        this.code = code;
        this.secretCode = secretCode;
    }
}

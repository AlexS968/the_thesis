package application.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //дата и время генерации кода капчи
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDate time;

    //код, отображаемый на картинке капчи
    @Column(length = 16, nullable = false)
    private String code;

    //код, передаваемый в параметре
    @NotBlank
    @Column(name = "secret_code", length = 16, nullable = false)
    private String secretCode;

}

package application.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //дата и время генерации кода капчи
    @Column(columnDefinition = "DATETIME", nullable = false)
    private LocalDate time;

    //код, отображаемый на картинке капчи
    @Column(columnDefinition = "TINYTEXT", nullable = false)
    private String code;

    //код, передаваемый в параметре
    @NotBlank
    @Column(name = "secret_code", columnDefinition = "TINYTEXT", nullable = false)
    private String secretCode;

}

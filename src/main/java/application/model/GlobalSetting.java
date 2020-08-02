package application.model;

import javax.persistence.*;

@Entity
@Table(name = "global_settings")
public class GlobalSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //системное имя настройки
    @Column(nullable = false)
    private String code;

    //название настройки
    @Column(nullable = false)
    private String name;

    //значение настройки
    @Column(nullable = false)
    private String value;

}

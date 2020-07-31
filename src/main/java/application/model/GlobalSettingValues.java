package application.model;

import javax.persistence.*;

@Entity
@Table(name = "global_setting_values")
public class GlobalSettingValues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //многопользовательский режим
    @Column(name = "multiuser_mode", nullable = false)
    private String multiuserMode;

    //премодерация постов
    @Column(name = "post_premoderation", nullable = false)
    private String postPremoderation;

    //показыватьвсем статистику блога
    @Column(name = "statistics_is_public", nullable = false)
    private String statisticsIsPublic;

}

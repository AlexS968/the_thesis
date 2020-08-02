package application.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //является ли пользователь модератором
    // (может ли править глобальные настройки сайта и модерировать посты)
    @Column(name = "is_moderator", columnDefinition = "BOOLEAN", nullable = false)
    private boolean isModerator;

    //дата и время регистрации пользователя
    @Column(name = "reg_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDate regTime;

    //имя пользователя
    @NotBlank
    @Column(nullable = false)
    private String name;

    //e-mail пользователя
    @NotBlank
    @Column(nullable = false)
    private String email;

    //хэш пароля пользователя
    @NotBlank
    @Column(nullable = false)
    private String password;

    //код для восстановления пароля, может быть NULL
    private String code;

    //фотография (ссылка на файл), может быть NULL
    @Column(columnDefinition = "TEXT")
    private String photo;

    @OneToMany(mappedBy = "user")
    private Set<Post> userPosts;

    @OneToMany(mappedBy = "moderator")
    private Set<Post> moderatorDecisions;

    @OneToMany(mappedBy = "user")
    private Set<PostVote> postVotes;

    @OneToMany(mappedBy = "user")
    private Set<PostComment> postComments;
}

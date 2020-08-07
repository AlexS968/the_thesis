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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isModerator() {
        return isModerator;
    }

    public void setModerator(boolean moderator) {
        isModerator = moderator;
    }

    public LocalDate getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDate regTime) {
        this.regTime = regTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Set<Post> getUserPosts() {
        return userPosts;
    }

    public void setUserPosts(Set<Post> userPosts) {
        this.userPosts = userPosts;
    }

    public Set<Post> getModeratorDecisions() {
        return moderatorDecisions;
    }

    public void setModeratorDecisions(Set<Post> moderatorDecisions) {
        this.moderatorDecisions = moderatorDecisions;
    }

    public Set<PostVote> getPostVotes() {
        return postVotes;
    }

    public void setPostVotes(Set<PostVote> postVotes) {
        this.postVotes = postVotes;
    }

    public Set<PostComment> getPostComments() {
        return postComments;
    }

    public void setPostComments(Set<PostComment> postComments) {
        this.postComments = postComments;
    }
}

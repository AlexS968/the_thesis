package application.model;

import application.model.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "is_moderator", columnDefinition = "BOOLEAN", nullable = false)
    private boolean isModerator;
    @Column(name = "reg_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime regTime;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;
    @NotBlank
    @Column(nullable = false)
    private String email;
    @NotBlank
    @Column(nullable = false)
    private String password;
    private String code; //password recovery code
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

    public Role getRole() {
        return isModerator ? Role.MODERATOR : Role.USER;
    }

    public User(@NotBlank String name, @NotBlank String email, @NotBlank String password, LocalDateTime regTime, boolean isModerator) {
        this.isModerator = isModerator;
        this.regTime = regTime;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}

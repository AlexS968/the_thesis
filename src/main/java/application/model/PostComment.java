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
@Table(name = "post_comments")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostComment parentPostComment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; //which was commented
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //who commented
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime time; //date and time of the comment
    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text; //comment text

    public PostComment(PostComment parentPostComment, Post post, User user, LocalDateTime time, @NotBlank String text) {
        this.parentPostComment = parentPostComment;
        this.post = post;
        this.user = user;
        this.time = time;
        this.text = text;
    }
}

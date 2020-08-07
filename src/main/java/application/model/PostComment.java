package application.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_comments")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //комментарий, на который оставлен этот комментарий (может быть NULL,
    //если комментарий оставлен просто к посту
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private PostComment parentPostComment;

    //пост, к которому написан комментарий
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    //автор комментария
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //дата и время комментария
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime time;

    //текст комментария
    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PostComment getParentPostComment() {
        return parentPostComment;
    }

    public void setParentPostComment(PostComment parentPostComment) {
        this.parentPostComment = parentPostComment;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

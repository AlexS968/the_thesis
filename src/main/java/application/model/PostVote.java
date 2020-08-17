package application.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_votes")
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //тот, кто поставил лайк/дизлайк
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //пост, которому поставлен лайк/дизлайк
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    //дата и время лайка/дизлайка
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime time;

    //лайк или дизлайк: true или false
    @Column(name = "value", columnDefinition = "BOOLEAN", nullable = false)
    private boolean value;

    public PostVote() {
    }

    public PostVote(User user, Post post, LocalDateTime time, boolean value) {
        this.user = user;
        this.post = post;
        this.time = time;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}

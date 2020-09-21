package application.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "post_votes")
public class PostVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //who liked
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; //which was liked
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime time; //date and time of the like
    @Column(name = "value", columnDefinition = "BOOLEAN", nullable = false)
    private boolean value; //like or dislike: true or false

    public PostVote(User user, Post post, LocalDateTime time, boolean value) {
        this.user = user;
        this.post = post;
        this.time = time;
        this.value = value;
    }
}

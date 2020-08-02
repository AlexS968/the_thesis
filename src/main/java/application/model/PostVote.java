package application.model;

import javax.persistence.*;
import java.time.LocalDate;

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
    private LocalDate time;

    //лайк или дизлайк: true или false
    @Column(name = "value", columnDefinition = "BOOLEAN", nullable = false)
    private boolean value;
}

package application.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "post_votes")
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //тот, кто поставил лайк/дизлайк
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //пост, которому поставлен лайк/дизлайк
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    //дата и время лайка/дизлайка
    @Column(columnDefinition = "DATETIME", nullable = false)
    private LocalDate time;

    //лайк или дизлайк: 1 или -1
    @Column(name = "value", columnDefinition = "TINYINT", nullable = false)
    private int value;
}

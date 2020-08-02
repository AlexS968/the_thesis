package application.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

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
    private LocalDate time;

    //текст комментария
    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;
}

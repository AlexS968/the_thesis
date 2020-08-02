package application.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //скрыта или активна публикация
    @Column(name = "is_active", columnDefinition = "BOOLEAN", nullable = false)
    private boolean isActive;

    //статус модерации, по умолчанию значение "NEW"
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status",length = 8, columnDefinition = "default 'NEW'",nullable = false)
    @JsonProperty(value = "moderation_status")
    private ModerationStatus moderationStatus;

    //ID пользователя-модератора, принявшего решение, или NULL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    //автор поста
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //дата и время публикации поста
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDate time;

    //заголовок поста
    @NotBlank
    @Column(nullable = false)
    private String title;

    //текст поста
    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    //количество просмотров поста
    @Column(name = "view_count", nullable = false)
    private int viewCount;

    //коллекция лайков
    @OneToMany
    @JoinColumn(name = "post_id", referencedColumnName = "id", insertable = false, updatable = false)
    @Where(clause = "value = true")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Set<PostVote> likeVotes;

    //коллекция дизлайков
    @OneToMany
    @JoinColumn(name = "post_id", referencedColumnName = "id", insertable = false, updatable = false)
    @Where(clause = "value = false")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Set<PostVote> dislikeVotes;

    //коллекция тэгов
    @OneToMany(mappedBy = "post")
    private Set<TagToPost> tagToPosts;

    //коллекция комментариев
    @OneToMany(mappedBy = "post")
    private Set<PostComment> postComments;

    public long getLikes() {
        return likeVotes.size();
    }

    public long dislikeVotes() {
        return dislikeVotes.size();
    }
}

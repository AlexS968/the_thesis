package application.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    private int id;

    //скрыта или активна публикация: 0 или 1
    @Column(name = "is_active", columnDefinition = "TINYINT", nullable = false)
    private int isActive;

    //статус модерацииб по умолчанию значение "NEW"
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", columnDefinition = "enum('NEW','ACCEPTED','DECLINED') default 'NEW'")
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
    @Column(columnDefinition = "DATETIME", nullable = false)
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

    @OneToMany(mappedBy = "post")
    private Set<PostVote> postVotes;

    @OneToMany(mappedBy = "post")
    private Set<TagToPost> tagToPosts;

    @OneToMany(mappedBy = "post")
    private Set<PostComment> postComments;

}

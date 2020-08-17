package application.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    @Column(name = "moderation_status", length = 8, columnDefinition = "default 'NEW'", nullable = false)
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
    private LocalDateTime time;

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
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public Set<TagToPost> getTagToPosts() {
        return tagToPosts;
    }

    public void setTagToPosts(Set<TagToPost> tagToPosts) {
        this.tagToPosts = tagToPosts;
    }

    public Set<PostComment> getPostComments() {
        return postComments;
    }

    public void setPostComments(Set<PostComment> postComments) {
        this.postComments = postComments;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", tagToPosts=" + tagToPosts +
                '}';
    }
}

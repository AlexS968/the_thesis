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

    //is post hidden or active
    @Column(name = "is_active", columnDefinition = "BOOLEAN", nullable = false)
    private boolean isActive;

    //moderation status, default value "NEW"
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", length = 8, columnDefinition = "default 'NEW'", nullable = false)
    @JsonProperty(value = "moderation_status")
    private ModerationStatus moderationStatus;

    //moderator, who made decision
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    //post author
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //post date and time
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime time;

    //post title
    @NotBlank
    @Column(nullable = false)
    private String title;

    //post text
    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    //post views
    @Column(name = "view_count", nullable = false)
    private int viewCount;

    //likes
    @OneToMany
    @JoinColumn(name = "post_id", referencedColumnName = "id", insertable = false, updatable = false)
    @Where(clause = "value = true")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Set<PostVote> likeVotes;

    //dislikes
    @OneToMany
    @JoinColumn(name = "post_id", referencedColumnName = "id", insertable = false, updatable = false)
    @Where(clause = "value = false")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Set<PostVote> dislikeVotes;

    //tags
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<TagToPost> tagToPosts;

    //comments
    @OneToMany(mappedBy = "post")
    private Set<PostComment> postComments;

    public long getLikes() {
        return likeVotes == null ? 0 : likeVotes.size();
    }

    public long dislikeVotes() {
        return dislikeVotes == null ? 0 : dislikeVotes.size();
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
                "isActive=" + isActive +
                ", moderationStatus=" + moderationStatus +
                ", moderator=" + moderator +
                ", user=" + user +
                ", time=" + time +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", viewCount=" + viewCount +
                ", likeVotes=" + likeVotes +
                ", dislikeVotes=" + dislikeVotes +
                ", tagToPosts=" + tagToPosts +
                ", postComments=" + postComments +
                '}';
    }
}

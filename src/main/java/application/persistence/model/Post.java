package application.persistence.model;

import application.persistence.enums.ModerationStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "is_active", columnDefinition = "BOOLEAN", nullable = false)
    private boolean isActive; //is post hidden or active
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", length = 8, columnDefinition = "default 'NEW'", nullable = false)
    @JsonProperty(value = "moderation_status")
    private ModerationStatus moderationStatus; //moderation status, default value "NEW"
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderator_id")
    private User moderator; //moderator, who made decision
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //post author
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime time; //post date and time
    @NotBlank
    @Column(nullable = false)
    private String title; //post title
    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String text; //post text
    @Column(name = "view_count", nullable = false)
    private int viewCount; //post views
    @OneToMany
    @JoinColumn(name = "post_id", referencedColumnName = "id", insertable = false, updatable = false)
    @Where(clause = "value = true")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Set<PostVote> likeVotes; //likes
    @OneToMany
    @JoinColumn(name = "post_id", referencedColumnName = "id", insertable = false, updatable = false)
    @Where(clause = "value = false")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Set<PostVote> dislikeVotes; //dislikes
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<TagToPost> tagToPosts; //tags
    @OneToMany(mappedBy = "post")
    private Set<PostComment> postComments; //comments

    public long getLikesNumber() {
        return likeVotes == null ? 0 : likeVotes.size();
    }

    public long dislikeVotesNumber() {
        return dislikeVotes == null ? 0 : dislikeVotes.size();
    }
}

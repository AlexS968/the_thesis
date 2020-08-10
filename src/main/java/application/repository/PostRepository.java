package application.repository;

import application.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT * " +
            "FROM posts " +
            "WHERE is_active = ?1 AND moderation_status = ?2 AND time <= ?3 " +
            "ORDER BY time ASC", nativeQuery = true)
    List<Post> findAllByIsActiveAndModerationStatusAndTimeLessThanEqualAndOrderByTimeAsc
            (boolean isActive, String moderationStatus, Timestamp time);

    @Query(value = "SELECT * " +
            "FROM posts " +
            "WHERE is_active = ?1 AND moderation_status = ?2 AND time <= ?3 " +
            "ORDER BY time DESC", nativeQuery = true)
    List<Post> findAllByIsActiveAndModerationStatusAndTimeLessThanEqualAndOrderByTimeDes
            (boolean isActive, String moderationStatus, Timestamp time);

    @Query(value = "SELECT *, " +
            "(SELECT COUNT(*) AS comments_number FROM post_comments WHERE post_comments.post_id = posts.id) " +
            "FROM posts " +
            "WHERE is_active = ?1 AND moderation_status = ?2 AND time <= ?3 " +
            "ORDER BY comments_number DESC", nativeQuery = true)
    List<Post> findAllByIsActiveAndModerationStatusAndTimeLessThanEqualAndOrderByCommentsNumberDes
            (boolean isActive, String moderationStatus, Timestamp time);

    @Query(value = "SELECT *, " +
            "(SELECT COUNT(*) AS likes_number FROM post_votes WHERE post_votes.post_id = posts.id) " +
            "FROM posts " +
            "WHERE is_active = ?1 AND moderation_status = ?2 AND time <= ?3 " +
            "ORDER BY likes_number DESC", nativeQuery = true)
    List<Post> findAllByIsActiveAndModerationStatusAndTimeLessThanEqualAndOrderByLikesNumberDes
            (boolean isActive, String moderationStatus, Timestamp time);

    @Query(value = "SELECT * " +
            "FROM posts " +
            "WHERE is_active = ?1 AND moderation_status = ?2 AND time <= ?3 AND title ILIKE ?4 " +
            "ORDER BY time DESC", nativeQuery = true)
    List<Post> findAllByIsActiveAndModerationStatusAndTimeLessThanEqualAndTitleContainingAndOrderByTimeDes
            (boolean isActive, String moderationStatus, Timestamp time, String query);

    @Query(value = "SELECT p.* FROM posts As p, tag2post AS t2p, tags As t " +
            "WHERE p.id = t2p.post_id AND t.id = t2p.tag_id AND p.is_active = ?1 AND " +
            "p.moderation_status = ?2 AND p.time <= ?3 AND t.name = ?4 " +
            "ORDER BY p.time DESC", nativeQuery = true)
    List<Post> findAllPostsByTag
            (boolean isActive, String moderationStatus, Timestamp time, String query);

    @Query(value = "SELECT t.name AS name, COUNT(p.*) AS total " +
            "FROM posts As p, tag2post AS t2p, tags As t " +
            "WHERE p.id = t2p.post_id AND t.id = t2p.tag_id GROUP BY name", nativeQuery = true)
    List<IPostCount> countPostsByTag();

    @Query(value = "SELECT to_char(time, 'YYYY-MM-DD') AS name, COUNT(*)  AS total " +
            "FROM posts " +
            "WHERE time >= ?1 AND time <= ?2 " +
            "GROUP BY name ORDER BY name", nativeQuery = true)
    List<IPostCount> countPostsByDay(Timestamp from, Timestamp to);

    @Query(value = "SELECT * " +
            "FROM posts " +
            "ORDER BY time ASC LIMIT 1", nativeQuery = true)
    Post findEarliestPost();

    @Query(value = "SELECT * FROM posts " +
            "WHERE is_active = ?1 AND moderation_status = ?2 " +
            "AND time <= ?3 AND to_char(time, 'YYYY-MM-DD') = ?4", nativeQuery = true)
    List<Post> findAllPostsByDate
            (boolean isActive, String moderationStatus, Timestamp time, String date);

    @Query(value = "SELECT * FROM posts ORDER BY time ASC", nativeQuery = true)
    List<Post> findAllOrderByTimeAsc();
}

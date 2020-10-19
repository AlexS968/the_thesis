package application.persistence.repository;

import application.persistence.model.TagToPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TagToPostRepository extends JpaRepository<TagToPost, Long> {

    @Query(value = "SELECT * FROM tag2post " +
            "WHERE tag_id = ?1 AND post_id = ?2", nativeQuery = true)
    Optional<TagToPost> findByTagIdAndByPostId(long tagId, long postId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tag2post WHERE post_id = ?1", nativeQuery = true)
    void deleteAllByPostId(long postId);
}

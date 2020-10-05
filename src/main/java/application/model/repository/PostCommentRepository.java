package application.model.repository;

import application.model.PostComment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PostCommentRepository extends CrudRepository<PostComment, Long> {

    @Query(value = "SELECT * FROM post_comments " +
            "WHERE post_id = ?1 ORDER BY time", nativeQuery = true)
    List<PostComment> findAllByPost(long id);

    @Query(value = "SELECT MAX(id) FROM post_comments", nativeQuery = true)
    long findMaxId();
}

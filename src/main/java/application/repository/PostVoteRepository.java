package application.repository;

import application.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Long> {

    @Query(value = "SELECT * " +
            "FROM post_votes " +
            "WHERE post_id = ?1 AND user_id = ?2", nativeQuery = true)
    PostVote findByPostIdAndByUserId(long postId, long userId);
}

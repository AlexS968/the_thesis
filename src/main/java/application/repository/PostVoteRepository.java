package application.repository;

import application.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Long> {

    @Query(value = "SELECT * " +
            "FROM post_votes " +
            "WHERE post_id = ?1 AND user_id = ?2", nativeQuery = true)
    Optional<PostVote> findByPostIdAndByUserId(long postId, long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM post_votes " +
            "WHERE post_id = ?1 AND user_id = ?2", nativeQuery = true)
    void deleteAllByPostIdAndByUserId(long postId, long userId);
}

package application.persistence.repository;

import application.persistence.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Override
    List<Tag> findAll();

    Optional<Tag> findByName(String name);

    List<Tag> findAllByNameContaining(String name);

    @Query(value = "SELECT t.name AS total FROM tags As t, tag2post AS t2p " +
            "WHERE t2p.post_id = ?1 AND t.id = t2p.tag_id", nativeQuery = true)
    List<String> findAllByPost(long id);
}

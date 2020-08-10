package application.repository;

import application.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {

    @Override
    List<Tag> findAll();

    @Query
    List<Tag> findAllByNameContaining(String name);

    @Query(value = "SELECT t.name AS total FROM tags As t, tag2post AS t2p " +
            "WHERE t2p.post_id = ?1 AND t.id = t2p.tag_id", nativeQuery = true)
    List<String> findAllByPost(long id);
}

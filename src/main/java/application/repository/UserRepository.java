package application.repository;

import application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query
    Optional<User> findByEmail(String email);

    @Query(value = "UPDATE users SET code = ?1 WHERE id = ?2", nativeQuery = true)
    void setUserCodeById(String code, long id);

}

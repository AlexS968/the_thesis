package application.model.repository;

import application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    Optional<User> findById(Long id);

    Optional<User> findByName(String username);

    Optional<User> findByCode(String code);

    Optional<User> findByEmail(String email);

    @Query(value = "UPDATE users SET code = ?1 WHERE id = ?2", nativeQuery = true)
    void setUserCodeById(String code, long id);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM users WHERE email = ?1)", nativeQuery = true)
    boolean isUserEmailExist(String email);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM users WHERE name = ?1)", nativeQuery = true)
    boolean checkUserByName(String name);
}

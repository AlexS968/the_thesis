package application.model.repository;

import application.model.CaptchaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface CaptchaCodeRepository extends JpaRepository<CaptchaCode, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM captcha_codes WHERE time <= ?1", nativeQuery = true)
    void deleteAllOld(Timestamp time);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM captcha_codes WHERE time <= ?1)", nativeQuery = true)
    boolean checkOldCaptcha(Timestamp time);

    @Query
    Optional<CaptchaCode> findByCode(String code);
}

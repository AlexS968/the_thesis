package application.repository;

import application.model.GlobalSetting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalSettingRepository
        extends CrudRepository<GlobalSetting, Long> {

    Optional<GlobalSetting> findByCode(String code);
}

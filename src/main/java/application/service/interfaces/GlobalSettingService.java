package application.service.interfaces;

import application.model.GlobalSetting;
import application.model.User;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Set;

public interface GlobalSettingService {

    Set<GlobalSetting> getGlobalSettings();

    void saveGlobalSettings(Set<GlobalSetting> settings, Principal principal);

    void deleteGlobalSetting(long id);

    boolean statisticsIsPublic();
}

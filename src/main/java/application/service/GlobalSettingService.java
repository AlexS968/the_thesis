package application.service;

import application.persistence.model.GlobalSetting;

import java.security.Principal;
import java.util.Set;

public interface GlobalSettingService {

    Set<GlobalSetting> getGlobalSettings();

    void saveGlobalSettings(Set<GlobalSetting> settings, Principal principal);

    void deleteGlobalSetting(long id);

    boolean statisticsIsPublic();

    boolean postPreModerationEnabled();

    boolean multiUserModeEnabled();
}

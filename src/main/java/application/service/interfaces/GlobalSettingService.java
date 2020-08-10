package application.service.interfaces;

import application.model.GlobalSetting;

import java.util.Set;

public interface GlobalSettingService {

    Set<GlobalSetting> getGlobalSettings();

    GlobalSetting saveGlobalSetting(GlobalSetting globalSettingValues);

    void deleteGlobalSetting(long id);
}

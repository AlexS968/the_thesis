package application.service.mapper;

import application.api.request.GlobalSettingRequest;
import application.api.response.GlobalSettingResponse;
import application.persistence.model.GlobalSetting;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class GlobalSettingMapper {

    public Set<GlobalSetting> convertToEntity(GlobalSettingRequest request) {
        Set<GlobalSetting> globalSettings = new HashSet<>();
        globalSettings.add(new GlobalSetting("MULTIUSER_MODE", "",
                request.isMultiuserMode() ? "Yes" : "No"));
        globalSettings.add(new GlobalSetting("POST_PREMODERATION", "",
                request.isPostPremoderation() ? "Yes" : "No"));
        globalSettings.add(new GlobalSetting("STATISTICS_IS_PUBLIC", "",
                request.isStatisticsIsPublic() ? "Yes" : "No"));
        return globalSettings;
    }

    public GlobalSettingResponse convertToDto(Set<GlobalSetting> globalSettings) {
        GlobalSettingResponse response = new GlobalSettingResponse();
        for (GlobalSetting setting : globalSettings) {
            boolean value = setting.getValue().equals("Yes");
            switch (setting.getCode()) {
                case "MULTIUSER_MODE":
                    response.setMultiuserMode(value);
                    break;
                case "POST_PREMODERATION":
                    response.setPostPremoderation(value);
                    break;
                case "STATISTICS_IS_PUBLIC":
                    response.setStatisticsIsPublic(value);
                    break;
            }
        }
        return response;
    }
}

package application.service.impl;

import application.model.GlobalSetting;
import application.repository.GlobalSettingRepository;
import application.service.GlobalSettingService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class GlobalSettingServiceImpl implements GlobalSettingService {

    private final GlobalSettingRepository globalSettingRepository;

    public GlobalSettingServiceImpl(GlobalSettingRepository globalSettingRepository) {
        this.globalSettingRepository = globalSettingRepository;
    }

    @Override
    public Set<GlobalSetting> getGlobalSettings() {
        return StreamSupport.stream(globalSettingRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    @Override
    public GlobalSetting saveGlobalSetting(GlobalSetting globalSetting) {
        return globalSettingRepository.save(globalSetting);
    }

    @Override
    public void deleteGlobalSetting(long id) {
        globalSettingRepository.deleteById(id);
    }

    public boolean statisticsIsPublic() {
        return Objects.requireNonNull(StreamSupport.stream(globalSettingRepository.findAll().spliterator(), false)
                .filter(globalSetting -> globalSetting.getCode().equals("STATISTICS_IS_PUBLIC"))
                .findFirst().orElse(null)).getValue().equals("YES");
    }
}

package application.service.impl;

import application.exception.BadRequestException;
import application.persistence.model.GlobalSetting;
import application.persistence.model.User;
import application.persistence.repository.GlobalSettingRepository;
import application.service.GlobalSettingService;
import application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GlobalSettingServiceImpl implements GlobalSettingService {

    private final GlobalSettingRepository globalSettingRepository;
    private final UserService userService;

    @Override
    public Set<GlobalSetting> getGlobalSettings() {
        return StreamSupport.stream(globalSettingRepository.findAll()
                .spliterator(), false).collect(Collectors.toSet());
    }

    @Override
    public void saveGlobalSettings(Set<GlobalSetting> settings, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        //if user is moderator save settings
        if (user.isModerator()) {
            for (GlobalSetting setting : settings) {
                GlobalSetting globalSetting = globalSettingRepository
                        .findByCode(setting.getCode())
                        .orElseThrow(EntityNotFoundException::new);
                globalSetting.setValue(setting.getValue());
                globalSettingRepository.save(globalSetting);
            }
        } else {
            throw new BadRequestException("User is not moderator");
        }
    }

    @Override
    public void deleteGlobalSetting(long id) {
        globalSettingRepository.deleteById(id);
    }

    @Override
    public boolean statisticsIsPublic() {
        return Objects.requireNonNull(StreamSupport.stream(globalSettingRepository.findAll().spliterator(), false)
                .filter(globalSetting -> globalSetting.getCode().equals("STATISTICS_IS_PUBLIC"))
                .findFirst().orElse(null)).getValue().equals("Yes");
    }

    @Override
    public boolean postPreModerationEnabled() {
        return Objects.requireNonNull(StreamSupport.stream(globalSettingRepository.findAll().spliterator(), false)
                .filter(globalSetting -> globalSetting.getCode().equals("POST_PREMODERATION"))
                .findFirst().orElse(null)).getValue().equals("Yes");
    }

    @Override
    public boolean multiUserModeEnabled() {
        return Objects.requireNonNull(StreamSupport.stream(globalSettingRepository.findAll().spliterator(), false)
                .filter(globalSetting -> globalSetting.getCode().equals("MULTIUSER_MODE"))
                .findFirst().orElse(null)).getValue().equals("Yes");
    }
}

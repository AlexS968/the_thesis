package application.service;

import application.exception.BadRequestException;
import application.exception.UserUnauthenticatedException;
import application.model.GlobalSetting;
import application.model.User;
import application.repository.GlobalSettingRepository;
import application.repository.UserRepository;
import application.service.interfaces.GlobalSettingService;
import application.service.interfaces.UserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class GlobalSettingServiceImpl implements GlobalSettingService {

    private final GlobalSettingRepository globalSettingRepository;
    private final UserService userService;

    public GlobalSettingServiceImpl(GlobalSettingRepository globalSettingRepository, UserRepository userRepository, UserService userService) {
        this.globalSettingRepository = globalSettingRepository;
        this.userService = userService;
    }

    @Override
    public Set<GlobalSetting> getGlobalSettings() {
        return StreamSupport.stream(globalSettingRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    @Override
    public void saveGlobalSettings(Set<GlobalSetting> settings, HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(UserUnauthenticatedException::new);
        //if user is moderator save settings
        if (user.isModerator()) {
            for (GlobalSetting setting : settings) {
                GlobalSetting globalSetting = globalSettingRepository.findByCode(setting.getCode());
                globalSetting.setValue(setting.getValue());
                globalSettingRepository.save(globalSetting);
            }
        } else {
            throw new BadRequestException("User is not moderator!");
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
}

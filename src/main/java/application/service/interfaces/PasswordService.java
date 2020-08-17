package application.service.interfaces;

import application.api.request.ChangePasswordRequest;

public interface PasswordService {

    void changePassword(ChangePasswordRequest request);
}

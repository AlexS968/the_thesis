package application.service.interfaces;

import application.api.request.ChangePasswordRequest;
import application.api.request.PasswordRestoreRequest;
import application.api.response.ResultResponse;

public interface PasswordService {

    ResultResponse changePassword(ChangePasswordRequest request);

    ResultResponse restorePassword(PasswordRestoreRequest request);
}

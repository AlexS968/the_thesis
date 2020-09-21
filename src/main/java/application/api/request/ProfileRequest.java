package application.api.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileRequest {
    private Integer removePhoto;
    private String password;
    private String name;
    private String email;
}

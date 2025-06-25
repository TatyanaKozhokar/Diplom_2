package data;

import lombok.Data;

@Data
public class LoginData {
    private String email;
    private String password;

    public LoginData(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

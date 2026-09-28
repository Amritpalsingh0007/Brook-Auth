package com.akal.Brook_Auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class UserInfoDto {
    @NotEmpty(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username should be 3 to 20 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,20}$", message = "Username should only consists of letters and digits")
    private String username;
    @NotEmpty(message="Password is required")
    @Size(min = 8, max = 20, message = "Password should be 8 to 20 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$", message = "Password should consists of at least one digit, small and capital letter and a special character")
    private String password;
    @Email
    private String emailID;
    @Size(min = 3, max = 25)
    private String firstName;
    @Size(min = 3, max = 25)
    private String lastName;
}

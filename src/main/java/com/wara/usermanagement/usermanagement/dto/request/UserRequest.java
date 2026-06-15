package com.wara.usermanagement.usermanagement.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    @NotBlank(message = "must not be null or empty")
    private String name;
    @NotBlank(message = "must not be null or empty")
    private String username;
    @NotBlank(message = "must not be null or empty")
    private String email;
    private String phone;
    private String website;
}

package com.example.crudjob.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDTO {

    private Long userId;
    private String username;
    private String accessToken;
    private String refreshToken;
    private Set<String> roles;
    private Set<String> permissions;
    private String tokenType;
    private Long expiresIn;
}

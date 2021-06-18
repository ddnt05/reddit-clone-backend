package com.example.redditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccessResponseDTO extends LoginResponseDTO {
    private String token;

    public LoginSuccessResponseDTO(String status, String token) {
        super(status);
        this.token = token;
    }
}

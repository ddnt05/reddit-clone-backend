package com.example.redditclone.controller;

import com.example.redditclone.dto.LoginRequestDTO;
import com.example.redditclone.dto.LoginResponseDTO;
import com.example.redditclone.dto.LoginSuccessResponseDTO;
import com.example.redditclone.dto.RegisterRequestDTO;
import com.example.redditclone.exception.InvalidPasswordException;
import com.example.redditclone.exception.MissingParameterException;
import com.example.redditclone.exception.UserDoesNotExistException;
import com.example.redditclone.exception.VerificationTokenNotFoundException;
import com.example.redditclone.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequestDTO registerRequest) {
        authService.signUp(registerRequest);
        return new ResponseEntity<>("User Registration Successful", OK);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) throws UserDoesNotExistException, VerificationTokenNotFoundException {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activation Successful", OK);
    }

    @PostMapping("/login")
    public ResponseEntity<? extends LoginResponseDTO> login (@RequestBody LoginRequestDTO loginRequestDTO) throws InvalidPasswordException, MissingParameterException {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }


}

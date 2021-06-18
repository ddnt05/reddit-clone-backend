package com.example.redditclone.service;

import com.example.redditclone.dto.LoginRequestDTO;
import com.example.redditclone.dto.LoginResponseDTO;
import com.example.redditclone.dto.LoginSuccessResponseDTO;
import com.example.redditclone.dto.RegisterRequestDTO;

import com.example.redditclone.exception.*;
import com.example.redditclone.model.NotificationEmail;
import com.example.redditclone.model.User;
import com.example.redditclone.model.VerificationToken;
import com.example.redditclone.repository.UserRepository;
import com.example.redditclone.repository.VerificationTokenRepository;
import com.example.redditclone.security.JwtUtil;
import com.example.redditclone.security.RedditUserDetails;
import com.example.redditclone.security.RedditUserDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class AuthService {


    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final RedditUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository, VerificationTokenRepository verificationTokenRepository, MailService mailService, RedditUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.mailService = mailService;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }


    @Transactional
    public void signUp(RegisterRequestDTO registerRequestDTO) throws SpringRedditException {
        User user = modelMapper.map(registerRequestDTO, User.class);
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);

        String token = getVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));

    }

    private String getVerificationToken(User user) {
        VerificationToken verificationToken = new VerificationToken(UUID.randomUUID().toString(), user);
        verificationTokenRepository.save(verificationToken);
        return verificationToken.getToken();
    }


    public void verifyAccount(String token) throws VerificationTokenNotFoundException, UserDoesNotExistException {
        VerificationToken verificationToken = verificationTokenRepository.findVerificationTokenByToken(token)
                .orElseThrow(VerificationTokenNotFoundException::new);
        getUserAndEnable(verificationToken.getUser());
    }

    private void getUserAndEnable(User user) throws UserDoesNotExistException {
        User actualUser = userRepository.findById(user.getUserId()).orElseThrow(UserDoesNotExistException::new);
        userRepository.save(actualUser).setEnabled(true);
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) throws InvalidPasswordException, MissingParameterException {
        readLoginRequest(loginRequestDTO);
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.getUsername());
        checkPassword(loginRequestDTO, userDetails);
        return new LoginSuccessResponseDTO("ok",getToken(loginRequestDTO.getUsername()));
    }

    public Map<String, String> loginValidate(String username, String password) {
        Map<String, String> missingFields = new HashMap<>();
        missingFields.put("username", username);
        missingFields.put("password", password);
        return missingFields;
    }

    public void setupValidate(Map<String, String> fields) throws MissingParameterException {
        List<String> missingFields = new ArrayList<>();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (entry.getValue() == null || entry.getValue().equals("")) {
                missingFields.add(entry.getKey());
            }
        }
        if (!missingFields.isEmpty()) {
            String joined = String.join(", ", missingFields);
            throw new MissingParameterException("Missing parameter(s): " + joined + "!");
        }
    }

    public void readLoginRequest(LoginRequestDTO loginRequest)
            throws MissingParameterException {
        if (loginRequest == null) {
            throw new MissingParameterException("Missing parameter(s): password, username!");
        }

        Map<String, String> loginDetailsHM =
                loginValidate(loginRequest.getUsername(), loginRequest.getPassword());
        setupValidate(loginDetailsHM);
    }

    private void checkPassword(LoginRequestDTO loginRequest, UserDetails userDetails) throws InvalidPasswordException {
        if (!passwordEncoder.matches(loginRequest.getPassword(),userDetails.getPassword())) {
            throw new InvalidPasswordException();
        }
    }

    public String getToken(String username) {
        return jwtUtil.generateToken(username);
    }
}

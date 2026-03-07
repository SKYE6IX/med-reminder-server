package com.medreminder.medreminder_server.application.security;


import com.medreminder.medreminder_server.application.dtos.user.AuthResponse;
import com.medreminder.medreminder_server.application.dtos.user.LoginRequest;
import com.medreminder.medreminder_server.application.dtos.user.RegisterUserRequest;
import com.medreminder.medreminder_server.domain.UserService;
import com.medreminder.medreminder_server.domain.model.User;
import com.medreminder.medreminder_server.infrastructure.entity.UserEntity;
import com.medreminder.medreminder_server.infrastructure.mapper.UserMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;


    public AuthService(AuthenticationManager authenticationManager,
                       UserService userService,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    public AuthResponse registerUserWithEmail(RegisterUserRequest request){

        User existingUser = userService.findUserByEmail(request.getEmail());

        if( existingUser != null ){
           throw new UserAlreadyExistsException(existingUser.getEmail());
        }

        String hashPassword = passwordEncoder.encode(request.getPassword());

        request.updatePasswordToHash(hashPassword);

        User newUser = userService.createUser(request);

//        Generate token for user
        String token = jwtUtil.generateToken(newUser.getEmail());

//        Generate refreshToken
        String refreshToken = jwtUtil.generateRefreshToken(newUser.getEmail());

        return new AuthResponse(newUser.getId(),newUser.getEmail(),token,refreshToken);
    }


    public AuthResponse loginUserWithEmail(LoginRequest loginRequest){

        String email = loginRequest.email();

        String password = loginRequest.password();

        User existingUser = userService.findUserByEmail(email);

        if (existingUser == null){
            throw new UsernameNotFoundException("Invalid email or password");
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        if(!auth.isAuthenticated()){
            throw new BadCredentialsException("Email or password is invalid");
        }

        UserEntity userEntity = (UserEntity) auth.getPrincipal();

        User user = userMapper.toDomain(userEntity);

        String token = jwtUtil.generateToken(user.getEmail());

        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new AuthResponse(user.getId(), user.getEmail(), token, refreshToken);
    }
}

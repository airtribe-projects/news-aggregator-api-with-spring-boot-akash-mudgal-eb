package learn.spring.newsaggregator.controller;

import jakarta.validation.Valid;
import learn.spring.newsaggregator.dto.JwtResponseDto;
import learn.spring.newsaggregator.dto.UserLoginDto;
import learn.spring.newsaggregator.dto.UserRegistrationDto;
import learn.spring.newsaggregator.entity.User;
import learn.spring.newsaggregator.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = authService.registerUser(registrationDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully!");
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserLoginDto loginDto) {
        try {
            JwtResponseDto jwtResponse = authService.authenticateUser(loginDto);
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.badRequest().body(error);
        }
    }
}

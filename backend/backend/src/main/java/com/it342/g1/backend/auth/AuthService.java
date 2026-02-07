package com.it342.g1.backend.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.it342.g1.backend.auth.dto.AuthResponse;
import com.it342.g1.backend.auth.dto.LoginRequest;
import com.it342.g1.backend.auth.dto.RegisterRequest;
import com.it342.g1.backend.auth.dto.UserResponse;
import com.it342.g1.backend.security.JwtService;
import com.it342.g1.backend.user.User;
import com.it342.g1.backend.user.UserRepository;

import org.springframework.http.HttpStatus;

@Service
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
		}

		User user = new User(
				request.getName(),
				request.getEmail(),
				passwordEncoder.encode(request.getPassword()));

		User saved = userRepository.save(user);
		String token = jwtService.generateToken(
				new org.springframework.security.core.userdetails.User(saved.getEmail(), saved.getPassword(),
						java.util.Collections.emptyList()));

		return new AuthResponse(token, toUserResponse(saved));
	}

	public AuthResponse login(LoginRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

		String token = jwtService.generateToken(
				new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
						java.util.Collections.emptyList()));

		return new AuthResponse(token, toUserResponse(user));
	}

	public UserResponse toUserResponse(User user) {
		return new UserResponse(user.getId(), user.getName(), user.getEmail());
	}
}

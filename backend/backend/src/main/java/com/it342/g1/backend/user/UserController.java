package com.it342.g1.backend.user;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.it342.g1.backend.auth.AuthService;
import com.it342.g1.backend.auth.dto.UserResponse;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private final UserRepository userRepository;
	private final AuthService authService;

	public UserController(UserRepository userRepository, AuthService authService) {
		this.userRepository = userRepository;
		this.authService = authService;
	}

	@GetMapping("/me")
	public ResponseEntity<UserResponse> me(Principal principal) {
		if (principal == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}

		User user = userRepository.findByEmail(principal.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		return ResponseEntity.ok(authService.toUserResponse(user));
	}
}

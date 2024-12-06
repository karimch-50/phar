package com.labo.fs.data.controller;
/*
package com.labo.fs.data.controller;

import com.labo.fs.data.dto.AuthRequest;
import com.labo.fs.data.dto.AuthReset;
import com.labo.fs.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.stream.Collectors;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RestController;

// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;

// import java.util.Date;
// import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;


	private static final String SECRET_KEY = "secretKey";
        // 30 day in milliseconds

    private static final long EXPIRATION_TIME = 864_000_00L * 30;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            System.out.println("Login request received for user: " + authRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // System.out.println("User authenticated: " + authentication.getName());
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // System.out.println("User roles: " + userDetails.getAuthorities());

            return ResponseEntity.ok(generateToken(authentication, userDetails));
        } catch (AuthenticationException e) {
            // System.out.println("Authentication failed for user: " + authRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication information found");
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                return ResponseEntity.ok(generateToken(authentication, userDetails));
            } else {
                System.out.println("Principal is not an instance of UserDetails: " + principal.getClass().getName());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unexpected principal type: " + principal.getClass().getName());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while refreshing the token");
        }
    }

    private String generateToken(Authentication authentication, UserDetails userDetails) {
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // reset password method
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody AuthReset authRequest) {
        // System.out.println("Password reset request received for user: " + authRequest.getUsername() +"old:" + authRequest.getPassword() + "new:" + authRequest.getNewPassword());

        // check if the old password is correct
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // if the old password is correct, set the new password
        if(authentication.isAuthenticated()){
            userDetailsService.updatePassword(authRequest.getUsername(), authRequest.getNewPassword());
            return ResponseEntity.ok("Password reset successful");
        }
        // failed to reset password
        return ResponseEntity.ok("Password reset failed");
    }

}
*/

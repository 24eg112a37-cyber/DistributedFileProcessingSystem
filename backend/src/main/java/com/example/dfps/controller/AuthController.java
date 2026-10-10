package com.example.dfps.controller;
import com.example.dfps.model.AppUser;
import com.example.dfps.repo.UserRepository;
import com.example.dfps.security.JwtService;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt; private final String adminEmail;
 public AuthController(UserRepository users,PasswordEncoder encoder,JwtService jwt,@Value("${app.admin.email}") String adminEmail){this.users=users;this.encoder=encoder;this.jwt=jwt;this.adminEmail=adminEmail;}
 public record RegisterRequest(@NotBlank String name,@Email @NotBlank String email,@Size(min=8) String password){}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
 @PostMapping("/register") public ResponseEntity<?> register(@RequestBody RegisterRequest r){
  if(users.findByEmailIgnoreCase(r.email()).isPresent()) return ResponseEntity.badRequest().body(Map.of("message","Email is already registered"));
  AppUser u=new AppUser();u.name=r.name().trim();u.email=r.email().trim().toLowerCase();u.passwordHash=encoder.encode(r.password());
  u.role=u.email.equalsIgnoreCase(adminEmail)?"ADMIN":"USER";users.save(u);
  return ResponseEntity.ok(payload(u));
 }
 @PostMapping("/login") public ResponseEntity<?> login(@RequestBody LoginRequest r){
  var found=users.findByEmailIgnoreCase(r.email());
  if(found.isEmpty()||!encoder.matches(r.password(),found.get().passwordHash)) return ResponseEntity.status(401).body(Map.of("message","Invalid email or password"));
  return ResponseEntity.ok(payload(found.get()));
 }
 private Map<String,Object> payload(AppUser u){return Map.of("token",jwt.generate(u),"user",Map.of("id",u.id,"name",u.name,"email",u.email,"role",u.role));}
}

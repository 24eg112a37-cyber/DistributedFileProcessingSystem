package com.example.dfps.service;
import com.example.dfps.model.AppUser;
import com.example.dfps.repo.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
@Service
public class CurrentUser {
 private final UserRepository users;
 public CurrentUser(UserRepository users){this.users=users;}
 public AppUser require(){
  var auth=SecurityContextHolder.getContext().getAuthentication();
  if(auth==null || auth.getName()==null) throw new RuntimeException("Authentication required");
  return users.findByEmailIgnoreCase(auth.getName()).orElseThrow(()->new RuntimeException("User not found"));
 }
 public boolean isAdmin(AppUser u){return "ADMIN".equals(u.role);}
}

package com.example.dfps.security;
import com.example.dfps.repo.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
@Component
public class JwtFilter extends OncePerRequestFilter {
 private final JwtService jwt; private final UserRepository users;
 public JwtFilter(JwtService jwt,UserRepository users){this.jwt=jwt;this.users=users;}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException {
  String h=req.getHeader("Authorization");
  if(h!=null && h.startsWith("Bearer ")) {
   try {
    String email=jwt.subject(h.substring(7));
    users.findByEmailIgnoreCase(email).ifPresent(u -> {
     var auth=new UsernamePasswordAuthenticationToken(u.email,null,List.of(new SimpleGrantedAuthority("ROLE_"+u.role)));
     SecurityContextHolder.getContext().setAuthentication(auth);
    });
   } catch(Exception ignored) { SecurityContextHolder.clearContext(); }
  }
  chain.doFilter(req,res);
 }
}

package com.example.dfps.controller;
import com.example.dfps.repo.*;
import com.example.dfps.service.CurrentUser;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/admin")
public class AdminController {
 private final UserRepository users; private final JobRepository jobs; private final WorkerRepository workers; private final CurrentUser current;
 public AdminController(UserRepository users,JobRepository jobs,WorkerRepository workers,CurrentUser current){this.users=users;this.jobs=jobs;this.workers=workers;this.current=current;}
 @GetMapping("/stats") public ResponseEntity<?> stats(){
  var u=current.require();if(!current.isAdmin(u))return ResponseEntity.status(403).body(Map.of("message","Administrator access required"));
  var all=jobs.findAll();
  return ResponseEntity.ok(Map.of("users",users.count(),"jobs",all.size(),"queued",all.stream().filter(j->"QUEUED".equals(j.status)).count(),"processing",all.stream().filter(j->"PROCESSING".equals(j.status)).count(),"completed",all.stream().filter(j->"COMPLETED".equals(j.status)).count(),"failed",all.stream().filter(j->"FAILED".equals(j.status)).count(),"workers",workers.count()));
 }
}

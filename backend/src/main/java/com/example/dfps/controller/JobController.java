package com.example.dfps.controller;
import com.example.dfps.model.*;
import com.example.dfps.repo.*;
import com.example.dfps.service.CurrentUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
@RestController @RequestMapping("/api/jobs")
public class JobController {
 private final JobRepository jobs; private final UserRepository users; private final WorkerRepository workers; private final CurrentUser current; private final Path uploadDir;
 public JobController(JobRepository jobs,UserRepository users,WorkerRepository workers,CurrentUser current,@Value("${app.upload-dir}") String dir){this.jobs=jobs;this.users=users;this.workers=workers;this.current=current;this.uploadDir=Paths.get(dir).toAbsolutePath().normalize();}
 @PostMapping("/upload") public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) throws Exception {
  AppUser user=current.require();
  if(file.isEmpty()) return ResponseEntity.badRequest().body(Map.of("message","Choose a file first"));
  Files.createDirectories(uploadDir);
  String original=Optional.ofNullable(file.getOriginalFilename()).orElse("upload.bin").replaceAll("[^a-zA-Z0-9._-]","_");
  String stored=UUID.randomUUID()+"-"+original; Path target=uploadDir.resolve(stored).normalize();
  if(!target.startsWith(uploadDir)) return ResponseEntity.badRequest().body(Map.of("message","Invalid filename"));
  Files.copy(file.getInputStream(),target,StandardCopyOption.REPLACE_EXISTING);
  ProcessingJob j=new ProcessingJob();j.owner=user;j.filename=original;j.storedPath=target.toString();j.status="QUEUED";j.createdAt=Instant.now();j.updatedAt=Instant.now();jobs.save(j);
  return ResponseEntity.ok(view(j));
 }
 @GetMapping public List<Map<String,Object>> list(){
  AppUser u=current.require(); List<ProcessingJob> all=current.isAdmin(u)?jobs.findAllByOrderByCreatedAtDesc():jobs.findByOwnerIdOrderByCreatedAtDesc(u.id);
  return all.stream().map(this::view).toList();
 }
 @GetMapping("/{id}") public ResponseEntity<?> get(@PathVariable Long id){
  AppUser u=current.require(); return jobs.findById(id).filter(j->current.isAdmin(u)||(j.owner!=null&&j.owner.id.equals(u.id)))
   .<ResponseEntity<?>>map(j->ResponseEntity.ok(view(j))).orElseGet(()->ResponseEntity.notFound().build());
 }
 private Map<String,Object> view(ProcessingJob j){
  Map<String,Object> m=new LinkedHashMap<>();m.put("id",j.id);m.put("filename",j.filename);m.put("status",j.status);m.put("resultText",j.resultText);m.put("worker",j.worker==null?null:j.worker.name);m.put("createdAt",j.createdAt);m.put("updatedAt",j.updatedAt);m.put("ownerEmail",j.owner==null?null:j.owner.email);return m;
 }
}

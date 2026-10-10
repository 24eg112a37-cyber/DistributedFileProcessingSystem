package com.example.dfps.controller;
import com.example.dfps.model.*;
import com.example.dfps.repo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.*;
@RestController @RequestMapping("/api/workers")
public class WorkerController {
 private final WorkerRepository workers; private final JobRepository jobs; private final long timeout;
 public WorkerController(WorkerRepository workers,JobRepository jobs,@Value("${app.worker.timeout-seconds}") long timeout){this.workers=workers;this.jobs=jobs;this.timeout=timeout;}
 public record WorkerRequest(String name){}
 public record ResultRequest(String workerName,Long jobId,String result,String status){}
 @PostMapping("/heartbeat") public Map<String,Object> heartbeat(@RequestBody WorkerRequest r){
  WorkerNode w=workers.findByName(r.name()).orElseGet(()->{WorkerNode n=new WorkerNode();n.name=r.name();return n;});
  w.lastHeartbeat=Instant.now();w.status="ONLINE";workers.save(w);return workerView(w);
 }
 @PostMapping("/next-job") public Map<String,Object> nextJob(@RequestBody WorkerRequest r){
  WorkerNode w=workers.findByName(r.name()).orElseGet(()->{WorkerNode n=new WorkerNode();n.name=r.name();return n;});
  w.lastHeartbeat=Instant.now();w.status="ONLINE";workers.save(w);
  var pending=jobs.findByStatusOrderByCreatedAtAsc("QUEUED");
  if(pending.isEmpty()) return Map.of("job", "none");
  ProcessingJob j=pending.get(0);j.status="PROCESSING";j.worker=w;j.updatedAt=Instant.now();w.activeJobs++;workers.save(w);jobs.save(j);
  return Map.of("job",Map.of("id",j.id,"filename",j.filename,"storedPath",j.storedPath));
 }
 @PostMapping("/result") public Map<String,Object> result(@RequestBody ResultRequest r){
  ProcessingJob j=jobs.findById(r.jobId()).orElseThrow();WorkerNode w=workers.findByName(r.workerName()).orElseThrow();
  if(j.worker==null||!j.worker.id.equals(w.id)) throw new IllegalArgumentException("Worker is not assigned this job");
  j.status="FAILED".equalsIgnoreCase(r.status())?"FAILED":"COMPLETED";j.resultText=r.result();j.updatedAt=Instant.now();jobs.save(j);
  w.activeJobs=Math.max(0,w.activeJobs-1);w.lastHeartbeat=Instant.now();workers.save(w);
  return Map.of("ok",true,"status",j.status);
 }
 @GetMapping public List<Map<String,Object>> list(){
  Instant cutoff=Instant.now().minusSeconds(timeout);
  return workers.findAll().stream().map(w->{if(w.lastHeartbeat==null||w.lastHeartbeat.isBefore(cutoff))w.status="OFFLINE";workers.save(w);return workerView(w);}).toList();
 }
 private Map<String,Object> workerView(WorkerNode w){return Map.of("id",w.id,"name",w.name,"status",w.status,"activeJobs",w.activeJobs,"lastHeartbeat",w.lastHeartbeat==null?"":w.lastHeartbeat.toString());}
}

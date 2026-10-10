package com.example.dfps.service;
import com.example.dfps.model.ProcessingJob;
import com.example.dfps.repo.JobRepository;
import com.example.dfps.repo.WorkerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.Instant;
@Component
public class WorkerHealthScheduler {
 private final WorkerRepository workers;private final JobRepository jobs;private final long timeout;
 public WorkerHealthScheduler(WorkerRepository workers,JobRepository jobs,@Value("${app.worker.timeout-seconds}") long timeout){this.workers=workers;this.jobs=jobs;this.timeout=timeout;}
 @Scheduled(fixedDelay=5000) public void checkWorkers(){
  Instant cutoff=Instant.now().minusSeconds(timeout);
  workers.findAll().forEach(w->{
   if(w.lastHeartbeat==null||w.lastHeartbeat.isBefore(cutoff)){
    w.status="OFFLINE";workers.save(w);
    jobs.findAll().stream().filter(j->"PROCESSING".equals(j.status)&&j.worker!=null&&j.worker.id.equals(w.id)).forEach(j->{j.status="QUEUED";j.worker=null;j.updatedAt=Instant.now();jobs.save(j);});
   }
  });
 }
}

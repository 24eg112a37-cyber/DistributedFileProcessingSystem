package com.example.dfpsworker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.nio.file.*;
import java.util.*;
@Component
public class WorkerLoop implements CommandLineRunner {
 @Value("${worker.name}") String name;
 @Value("${coordinator.url}") String base;
 @Value("${worker.poll-ms}") long pollMs;
 final RestTemplate http=new RestTemplate();
 @Override public void run(String... args) {
  Thread t=new Thread(()->{
   while(!Thread.currentThread().isInterrupted()){
    try { heartbeat(); processOne(); Thread.sleep(pollMs); }
    catch(Exception e){System.err.println("Worker cycle error: "+e.getMessage());try{Thread.sleep(pollMs);}catch(InterruptedException x){Thread.currentThread().interrupt();}}
   }
  },"worker-loop");t.setDaemon(false);t.start();
 }
 void heartbeat(){http.postForEntity(base+"/workers/heartbeat",Map.of("name",name),Map.class);}
 void processOne() throws Exception {
  Map<?,?> response=http.postForObject(base+"/workers/next-job",Map.of("name",name),Map.class);
  if(response==null||!(response.get("job") instanceof Map<?,?> job))return;
  Long id=((Number)job.get("id")).longValue();String path=String.valueOf(job.get("storedPath"));String result;
  try {
   Path p=Paths.get(path);String content=Files.isRegularFile(p)?Files.readString(p):"";
   result="Processed file '"+job.get("filename")+"'. Size="+(Files.exists(p)?Files.size(p):0)+" bytes. Text preview="+content.substring(0,Math.min(content.length(),500));
   http.postForEntity(base+"/workers/result",Map.of("workerName",name,"jobId",id,"result",result,"status","COMPLETED"),Map.class);
  } catch(Exception ex) {
   http.postForEntity(base+"/workers/result",Map.of("workerName",name,"jobId",id,"result","Processing failed: "+ex.getMessage(),"status","FAILED"),Map.class);
  }
 }
}

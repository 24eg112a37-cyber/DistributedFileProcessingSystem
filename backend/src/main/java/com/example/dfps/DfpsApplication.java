package com.example.dfps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class DfpsApplication {
 public static void main(String[] args) { SpringApplication.run(DfpsApplication.class, args); }
}

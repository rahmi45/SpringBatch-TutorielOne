package fr.atos.springbatchcsvtomysql.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
//@Slf4j
public class MyJobRunner implements CommandLineRunner {

    @Autowired
    private JobLauncher launcher;

    @Autowired
    private Job jobA;

    public void run(String... args) throws Exception {
        JobParameters jobParameters =
                new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

        launcher.run(jobA, jobParameters);
        System.out.println("JOB EXECUTION DONE !");
        //log.info("JOB EXECUTION DONE !");
    }
}

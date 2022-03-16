package fr.atos.springbatchcsvtomysql.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.util.Date;

public class MyJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution je) {
        System.out.println("Started Date and Time : " + new Date());
        System.out.println("Status at Starting : " + je.getStatus());
    }

    @Override
    public void afterJob(JobExecution je) {
        System.out.println("End Date and Time : " + new Date());
        System.out.println("Status at Ending : " + je.getStatus());
    }
}

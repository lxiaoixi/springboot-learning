package com.example.mybatis.util;

import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {

    private int count= 0;
    //@Scheduled(cron = "*/6 * * * * ?")
//    @Scheduled(cron = "0 0 0 0 0 0")
    public void process(){
        System.out.println("this is scheduler task runing  "+(count++));
    }

}

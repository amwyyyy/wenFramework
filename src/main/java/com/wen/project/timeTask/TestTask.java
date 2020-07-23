package com.wen.project.timeTask;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestTask {
	
	@Scheduled(cron="${cron.testTask}")
	public void doTask() {
		System.out.println("任务执行...");
	}
}

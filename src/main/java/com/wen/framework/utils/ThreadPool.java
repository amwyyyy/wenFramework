package com.wen.framework.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理类，不能new，由spring注入
 * @author huangwg
 *
 */
public class ThreadPool {
	private ThreadPoolExecutor threadPool = null;
	
	// 线程池维护线程的最少数量
	private Integer corePoolSize = 5;

	// 线程池维护线程的最大数量
	private Integer maxPoolSize = 20;

	// 线程池维护线程所允许的空闲时间
	private Integer keepAliveTime = 180;

	// 线程池所使用的缓冲队列大小
	private Integer workQueueSize = 5000;
	
	private ThreadPool() {}

	public synchronized ThreadPoolExecutor getInstance() {
		if (threadPool == null) {
			threadPool = new ThreadPoolExecutor(corePoolSize, 
					maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
					new ArrayBlockingQueue<Runnable>(workQueueSize), 
					new ThreadPoolExecutor.DiscardOldestPolicy());
		}
		return threadPool;
	}

	public void setCorePoolSize(Integer corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public void setMaxPoolSize(Integer maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public void setKeepAliveTime(Integer keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public void setWorkQueueSize(Integer workQueueSize) {
		this.workQueueSize = workQueueSize;
	}
}

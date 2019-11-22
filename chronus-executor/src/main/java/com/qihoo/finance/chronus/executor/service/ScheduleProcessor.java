package com.qihoo.finance.chronus.executor.service;

public interface ScheduleProcessor {
	/**
	 * 是否已经执行结束
	 * @return
	 */
	boolean isExecutorEnd();

	/**
	 * 停止任务处理器
	 * @throws Exception
	 */
	void stopProcessor();
}

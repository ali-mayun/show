package com.ty.task;

public class Task {
	//ͨ��taskId��������б�ʶ
    private int taskId;
    
    public Task(int taskId) {
        this.taskId = taskId;
    }

    public void doJob() {
        System.out.println("�߳�" + Thread.currentThread().getName() + "���ڴ�������");
    }

    public int getId() {        
        return taskId;
    }
}

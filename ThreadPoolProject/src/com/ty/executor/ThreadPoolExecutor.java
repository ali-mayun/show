package com.ty.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.ty.task.Task;

public class ThreadPoolExecutor {
	/*
     * BlockingQueue���������У�����������³���������
     *     1�����������ˣ�����в���ʱ��
     *     2�������п��ˣ������в���ʱ��
     * �����������̰߳�ȫ�ģ���Ҫʹ��������/�����ߵĳ���
     */
    private BlockingQueue<Task> blockingQueue;
    
    //�̳߳صĹ����߳���(������Ϊ���̳߳ص�����)
    private int poolSize = 0;
    
    //�̳߳صĺ�������(Ҳ���ǵ�ǰ�̳߳����������ڵ��̸߳���)
    private int coreSize = 0;
    
    /*
     * �˵ط�ʹ��volatile�ؼ��֣�volatile�Ĺ���ԭ���ǣ�����JVMά����˵��ÿ���̳߳��б����Ĺ����������Ƕ��ڼ����ά����˵��
     * ������Щ�������м�ֵ�����ڸ��ٻ����С�ͨ��volatile�ؼ��֣���֪ÿ���̸߳ı�˱���֮��������µ��ڴ���ȥ������ʹ��
     * �����е�����ʧЧ����������֤����ĳ���̸߳ı乫�б����������߳��ܼ�ʱ��ȡ�����µı���ֵ���Ӷ���֤�ɼ��ԡ�
     * ԭ�����£�
     *     1����ThreadPoolExecutorTest�в���shutDown������main�̲߳����˱���(���ڱ�����volatile���������Ի�����д���ڴ���)��
     *     2��Worker���߳�ͨ��while(!shutDown)���жϵ�ǰ�߳��Ƿ�Ӧ�ùرգ������ͨ��volatile��֤�ɼ��ԣ�ʹ�߳̿��Լ�ʱ�õ��رա�
     */
    private volatile boolean shutDown = false;
    
    public ThreadPoolExecutor(int size) {
        this.poolSize = size;
        //LinkedBlockingQueue�Ĵ�С����ָ������ָ����Ϊ�ޱ߽�ġ�
        blockingQueue = new LinkedBlockingQueue<>(poolSize);
    }
    
    public void execute(Task task) throws InterruptedException {
        if(shutDown == true) {
            return;
        }
        
        if(coreSize < poolSize) {
            /*
             * BlockingQueue�еĲ�����Ҫ��offer(obj)�Լ�put(obj)��������������put(obj)������������������벻�����Ͻ��У�
             * �����������offer(obj)���ǲ��벻�����Ͻ��У�����true��false��
             * �����е�Task������ʧ�����Բ���put(obj);
             */
            blockingQueue.put(task);
            produceWorker(task);
        }else {
            blockingQueue.put(task);
        }
    }

    private void produceWorker(Task task) throws InterruptedException {
        if(task == null) {
            throw new NullPointerException("�Ƿ������������task����Ϊ�գ�");
        }

        Thread thread = new Thread(new Worker());        
        thread.start();
        coreSize++;
    }
    
    /*
     * �����ж��̵߳ķ�������ʹ�ù�����������źţ������߳�ֹͣ���С�
     * 
     */
    public void shutDown() {
        shutDown = true;
    }
    
    /*
     * ���ڲ�����ʵ���ϵĹ����߳�
     * 
     */
    class Worker implements Runnable {

        @Override
        public void run() {        
            while(!shutDown) {    
                try {
                    //
                    blockingQueue.take().doJob();
                } catch (InterruptedException e) {                    
                    e.printStackTrace();
                }
            }            
            System.out.println("�̣߳�" + Thread.currentThread().getName() + "�˳����У�");
        }    
    }
}

package com.prowdloner.camera2libraryproject.prowdVO;

import android.os.Handler;
import android.os.HandlerThread;

// 쓰레드 관리 객체를 모아둔 VO 클래스
// HandlerThread 와 Handler 로 구성
public class Prowd_ThreadSet_VO {
    private String threadName;
    private HandlerThread handlerThread;
    private Handler handler;

    public HandlerThread getHandlerThread() {
        return handlerThread;
    }

    public void setHandlerThread(HandlerThread handlerThread) {
        this.handlerThread = handlerThread;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}

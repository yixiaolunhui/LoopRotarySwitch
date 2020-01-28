package com.dalong.library;

import android.os.Handler;
import android.os.Message;

/**
 * loopHandler 发送
 */
public abstract class LoopViewHandler extends Handler {

    private boolean isCanLoop = false;//是否要发送

    public long mLoopTime = 3000;//时间间隔

    public static final int MSG_WHAT = 1000;

    private Message msg = createMsg();//创建message

    /**
     * 构造方法
     *
     * @param time
     */
    public LoopViewHandler(int time) {
        this.mLoopTime = time;
    }

    /**
     * handler处理
     *
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what = MSG_WHAT) {
            case MSG_WHAT:
                if (isCanLoop) {
                    doScroll();
                    sendMsg();
                }
                break;
        }
        super.handleMessage(msg);
    }

    /**
     * 设置是否要发送
     *
     * @param loop
     */
    public void setLoop(boolean loop) {
        this.isCanLoop = loop;
        if (loop) {
            sendMsg();
        } else {
            try {
                removeMessages(MSG_WHAT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送message
     */
    private void sendMsg() {
        try {
            removeMessages(MSG_WHAT);
        } catch (Exception e) {
        }
        msg = createMsg();
        this.sendMessageDelayed(msg, mLoopTime);
    }

    /**
     * 创建message
     *
     * @return
     */
    public Message createMsg() {
        Message msg = new Message();
        msg.what = MSG_WHAT;
        return msg;
    }

    /**
     * 设置时间
     *
     * @param loopTime
     */
    public void setLoopTime(long loopTime) {
        this.mLoopTime = loopTime;
    }

    public long getLoopTime() {
        return mLoopTime;
    }

    /**
     * 返回是否可以发送
     *
     * @return
     */
    public boolean isLoop() {
        return isCanLoop;
    }

    public abstract void doScroll();
}

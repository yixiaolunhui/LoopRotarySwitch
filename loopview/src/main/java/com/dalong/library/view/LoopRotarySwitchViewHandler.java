package com.dalong.library.view;

import android.os.Handler;
import android.os.Message;

/**
 * loopHandler 发送
 */
public abstract class LoopRotarySwitchViewHandler extends Handler {

    private boolean loop = false;//是否要发送

    public long loopTime = 3000;//时间间隔

    public static final int msgid = 1000;//id

    private Message msg = createMsg();//创建message

    /**
     * 构造方法
     *
     * @param time
     */
    public LoopRotarySwitchViewHandler(int time) {
        this.loopTime = time;
    }

    /**
     * handler处理
     *
     * @param msg
     */
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what = msgid) {
            case msgid:
                if (loop) {
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
        this.loop = loop;
        if (loop) {
            sendMsg();
        } else {
            try {
                removeMessages(msgid);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 发送message
     */
    private void sendMsg() {
        try {
            removeMessages(msgid);
        } catch (Exception e) {
        }
        msg = createMsg();
        this.sendMessageDelayed(msg, loopTime);
    }

    /**
     * 创建message
     *
     * @return
     */
    public Message createMsg() {
        Message msg = new Message();
        msg.what = msgid;
        return msg;
    }

    /**
     * 设置时间
     *
     * @param loopTime
     */
    public void setLoopTime(long loopTime) {
        this.loopTime = loopTime;
    }

    public long getLoopTime() {
        return loopTime;
    }

    /**
     * 返回是否可以发送
     *
     * @return
     */
    public boolean isLoop() {
        return loop;
    }

    public abstract void doScroll();
}

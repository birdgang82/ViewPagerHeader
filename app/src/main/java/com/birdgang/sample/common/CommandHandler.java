package com.birdgang.sample.common;

import android.os.Handler;
import android.os.Message;

/**
 * Created by birdgang on 2016. 12. 19..
 */

public class CommandHandler {

    private static Handler handler = new Handler () {
        public void handleMessage (Message msg) {
            if (msg.obj instanceof Command) {
                ((Command)msg.obj).execute();
            }
        }
    };

    public void send (Command command) {
        Message message = handler.obtainMessage();
        message.obj = command;
        handler.sendMessage(message);
    }

    public void sendForDelay (Command command, int timeout) {
        Message message = handler.obtainMessage();
        message.obj = command;
        handler.sendMessageDelayed(message, timeout);
    }

}

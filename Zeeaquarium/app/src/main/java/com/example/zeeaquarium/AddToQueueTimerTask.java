package com.example.zeeaquarium;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import java.util.TimerTask;

public class AddToQueueTimerTask extends TimerTask {
    private RequestQueue queue;
    private Request request;

    public <T> AddToQueueTimerTask(RequestQueue queue, Request<T> request) {
        this.queue = queue;
        this.request = request;
    }
    @Override
    public void run() {
        queue.add(request);
    }
}

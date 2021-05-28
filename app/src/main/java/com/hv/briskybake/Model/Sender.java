package com.hv.briskybake.Model;

import com.google.android.gms.tasks.Task;

public class Sender {
    public Task<String> to;
    public Notification notification;

    public Sender(Task<String> to, Notification notification) {
        this.to = to;
        this.notification = notification;
    }
}

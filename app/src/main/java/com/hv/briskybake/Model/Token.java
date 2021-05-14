package com.hv.briskybake.Model;

import com.google.android.gms.tasks.Task;

public class Token {
    public String token;
    private boolean isServerToken;

    public Token(Task<String> token, boolean isServerToken) {
        this.token = token;
        this.isServerToken = isServerToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isServerToken() {
        return isServerToken;
    }

    public void setServerToken(boolean serverToken) {
        isServerToken = serverToken;
    }
}

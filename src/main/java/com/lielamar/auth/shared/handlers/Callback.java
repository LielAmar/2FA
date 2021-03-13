package com.lielamar.auth.shared.handlers;

public interface Callback {

    void execute();
    long getExecutionStamp();
}

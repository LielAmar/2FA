package com.lielamar.auth.api.communication;

public enum CommunicationMethod {
    // Single Server Communication
    NONE,

    // Proxy Server Communication
    PROXY,
    REDIS,
    RABBITMQ;
}

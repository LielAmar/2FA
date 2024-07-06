package com.lielamar.auth.core.stats;

import com.lielamar.auth.api.stats.IStatsTracker;

public final class StatsTracker implements IStatsTracker {
    private int communicationFailures;
    private int authenticationCount;

    public StatsTracker() {
        this.authenticationCount = 0;
        this.communicationFailures = 0;
    }

    @Override
    public int getAuthentications() {
        return authenticationCount;
    }

    public void setAuthentications(int count) {
        this.authenticationCount = count;
    }

    public void incrementAuthentications() {
        this.authenticationCount++;
    }

    @Override
    public int getCommunicationFailures() {
        return communicationFailures;
    }

    public void setCommunicationFailures(int count) {
        this.communicationFailures = count;
    }

    public void incrementCommunicationFailures() {
        this.communicationFailures++;
    }
}

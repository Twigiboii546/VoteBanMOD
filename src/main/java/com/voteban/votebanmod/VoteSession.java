package com.voteban.votebanmod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoteSession {
    public final UUID targetId;
    public final String targetName;
    public final UUID starterId;
    public final String reason;
    public final long startTimeMillis;

    public final Map<UUID, Boolean> votes = new HashMap<>();

    public VoteSession(UUID targetId, String targetName, UUID starterId, String reason) {
        this.targetId = targetId;
        this.targetName = targetName;
        this.starterId = starterId;
        this.reason = reason;
        this.startTimeMillis = System.currentTimeMillis();
    }

    public boolean hasExpired() {
        return System.currentTimeMillis() - startTimeMillis >= 60_000;
    }

    public int getYesVotes() {
        return (int) votes.values().stream().filter(v -> v).count();
    }

    public int getNoVotes() {
        return (int) votes.values().stream().filter(v -> !v).count();
    }
}
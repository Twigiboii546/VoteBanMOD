package com.voteban.votebanmod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanListEntry;
import java.util.Date;
import net.minecraft.server.players.NameAndId;


public class VoteManager {

    private static String banTime = "1h";

    public static void setBanTime(String time) {
        banTime = time;
    }

    public static String getBanTime() {
        return banTime;
    }
    private static VoteSession activeVote;

    public static boolean hasActiveVote() {
        return activeVote != null;
    }

    public static VoteSession getActiveVote() {
        return activeVote;
    }

    private static Date getBanExpiryDate() {
    String time = banTime.toLowerCase();

    if (time.equals("permanent") || time.equals("perm")) {
        return null;
    }

    if (time.equals("1d")) {
        return new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
    }

    return new Date(System.currentTimeMillis() + 60 * 60 * 1000);
}

    public static void startVote(MinecraftServer server, ServerPlayer starter, ServerPlayer target, String reason) {
        activeVote = new VoteSession(
                target.getUUID(),
                target.getName().getString(),
                starter.getUUID(),
                reason
        );

        server.getPlayerList().broadcastSystemMessage(
                Component.literal("Vote started to kick " + target.getName().getString()
                        + " for: " + reason
                        + " | Type /voteban yes or /voteban no"),
                false
        );
    }

    public static void vote(ServerPlayer player, boolean yes) {
        if (activeVote == null) {
            player.sendSystemMessage(Component.literal("No active vote."));
            return;
        }

        activeVote.votes.put(player.getUUID(), yes);
        player.sendSystemMessage(Component.literal("Vote counted."));
    }

    public static void registerTickEvent() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (activeVote != null && activeVote.hasExpired()) {
                finishVote(server);
            }
        });
    }

    private static void finishVote(MinecraftServer server) {
        int yes = activeVote.getYesVotes();
        int no = activeVote.getNoVotes();
        int total = yes + no;

        boolean passed = total >= 3 && yes >= Math.ceil(total * 0.7);

        if (passed) {
            ServerPlayer target = server.getPlayerList().getPlayer(activeVote.targetId);

            if (target != null) {
                Date created = new Date();
                Date expires = getBanExpiryDate();
                server.getPlayerList().getBans().add(
        new UserBanListEntry(
                new NameAndId(target.getUUID(), target.getName().getString()),
                created,
                "VoteBan",
                expires,
                activeVote.reason
        )
);

    target.connection.disconnect(
            Component.literal("You were banned by vote for: " + banTime + "\nReason: " + activeVote.reason)
    );
}
           

            server.getPlayerList().broadcastSystemMessage(
                    Component.literal("Vote passed. " + activeVote.targetName + " was kicked."),
                    false
            );
        } else {
            server.getPlayerList().broadcastSystemMessage(
                    Component.literal("Vote failed. Yes: " + yes + " No: " + no),
                    false
            );
        }

        activeVote = null;
    }
}
package com.voteban.votebanmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class VoteBanMod implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(ProtectedPlayers::load);

        VoteBanCommands.register();
        VoteManager.registerTickEvent();

        System.out.println("VoteBan mod loaded!");
    }
}
package com.voteban.votebanmod;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ProtectedPlayers {
    private static final Set<UUID> protectedPlayers = new HashSet<>();
    private static Path filePath;

    public static void load(MinecraftServer server) {
        filePath = server.getServerDirectory().resolve("voteban-protected.txt");

        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            protectedPlayers.clear();

            for (String line : Files.readAllLines(filePath)) {
                if (!line.isBlank()) {
                    protectedPlayers.add(UUID.fromString(line.trim()));
                }
            }

        } catch (IOException e) {
            System.out.println("Failed to load protected players file.");
            e.printStackTrace();
        }
    }

    public static void add(ServerPlayer player) {
        protectedPlayers.add(player.getUUID());
        save();
    }

    public static void remove(ServerPlayer player) {
        protectedPlayers.remove(player.getUUID());
        save();
    }

    public static boolean isProtected(ServerPlayer player) {
        return protectedPlayers.contains(player.getUUID());
    }

    private static void save() {
        try {
            Files.write(
                    filePath,
                    protectedPlayers.stream().map(UUID::toString).toList()
            );
        } catch (IOException e) {
            System.out.println("Failed to save protected players file.");
            e.printStackTrace();
        }
    }
}
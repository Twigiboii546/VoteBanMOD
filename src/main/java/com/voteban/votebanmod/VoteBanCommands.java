package com.voteban.votebanmod;

import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;


public class VoteBanCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(Commands.literal("voteban")

                    .then(Commands.literal("start")
                            .then(Commands.argument("target", EntityArgument.player())
                                    .then(Commands.argument("reason", StringArgumentType.greedyString())
                                            .executes(ctx -> {
                                                ServerPlayer starter = ctx.getSource().getPlayer();
                                                ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                                String reason = StringArgumentType.getString(ctx, "reason");

                                                if (VoteManager.hasActiveVote()) {
                                                    starter.sendSystemMessage(Component.literal("Vote already active."));
                                                    return 0;
                                                }

                                                if (ProtectedPlayers.isProtected(target)) {
                                                    starter.sendSystemMessage(Component.literal("This player is protected and cannot be vote-kicked."));
                                                    return 0;
                                                }

                                                VoteManager.startVote(ctx.getSource().getServer(), starter, target, reason);
                                                return 1;
                                            })
                                    )
                            )
                    )

                    .then(Commands.literal("yes").executes(ctx -> {
                        VoteManager.vote(ctx.getSource().getPlayer(), true);
                        return 1;
                    }))

                    .then(Commands.literal("no").executes(ctx -> {
                        VoteManager.vote(ctx.getSource().getPlayer(), false);
                        return 1;
                    }))

                    .then(Commands.literal("status").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        VoteSession vote = VoteManager.getActiveVote();

                        if (vote == null) {
                            player.sendSystemMessage(Component.literal("No vote active."));
                            return 0;
                        }

                        player.sendSystemMessage(Component.literal(
                                "Voting on " + vote.targetName +
                                        " | Yes: " + vote.getYesVotes() +
                                        " | No: " + vote.getNoVotes()
                        ));

                        return 1;
                    }))
            );

            dispatcher.register(Commands.literal("votebanadmin")
                 .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
                    .then(Commands.literal("add")
                   
                            .then(Commands.argument("player", EntityArgument.player())
                                    .executes(ctx -> {

                                        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");

                                        ProtectedPlayers.add(player);

                                        ctx.getSource().sendSuccess(
                                                () -> Component.literal(player.getName().getString() + " is now protected from vote bans."),
                                                true
                                        );

                                        return 1;
                                    })
                            )
                    )

                    .then(Commands.literal("remove")
                            .then(Commands.argument("player", EntityArgument.player())
                                    .executes(ctx -> {

                                        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");

                                        ProtectedPlayers.remove(player);

                                        ctx.getSource().sendSuccess(
                                                () -> Component.literal(player.getName().getString() + " is no longer protected from vote bans."),
                                                true
                                        );

                                        return 1;
                                    })
                            )
                    )
                    .then(Commands.literal("bantime")
        .then(Commands.argument("time", StringArgumentType.word())
                .executes(ctx -> {
                    String time = StringArgumentType.getString(ctx, "time").toLowerCase();

                    if (!time.equals("1h") && !time.equals("1d") && !time.equals("permanent") && !time.equals("perm")) {
                        ctx.getSource().sendFailure(Component.literal("Invalid ban time. Use: 1h, 1d, or permanent."));
                        return 0;
                    }

                    if (time.equals("perm")) {
                        time = "permanent";
                    }

                    VoteManager.setBanTime(time);

                    ctx.getSource().sendSuccess(
                            () -> Component.literal("Vote ban time set to: " + VoteManager.getBanTime()),
                            true
                    );

                    return 1;
                })
        )
)
            );
        });
    }
}
package com.katt.changedextras.common;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.common.debug.LatexDebugManager;
import com.katt.changedextras.entity.beasts.JammerEntity;
import com.katt.changedextras.network.ChangedExtrasNetwork;
import com.katt.changedextras.network.OpenLatexSpawnControlScreenPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings("deprecation")
public final class ChangedExtrasSpawnCommands {

    private ChangedExtrasSpawnCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cedebug")
                .executes(context -> toggleDebug(context.getSource())));

        dispatcher.register(Commands.literal("changedextras")
                .then(Commands.literal("choice")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            ChangedExtras.SpecialPlayerData specialData = ChangedExtras.SPECIAL_PLAYERS.get(player.getUUID());
                            if (specialData == null) {
                                context.getSource().sendFailure(Component.literal("§cYou do not have a special transfur starter available."));
                                return 0;
                            }
                            CompoundTag data = player.getPersistentData();
                            if (data.getBoolean(ChangedExtras.SPECIAL_CHOICE_MADE_TAG)) {
                                context.getSource().sendFailure(Component.literal("§cYou have already made your starter transfur choice."));
                                return 0;
                            }
                            ChangedExtras.sendTransfurPrompt(player, specialData);
                            return 1;
                        })
                        .then(Commands.literal("yes")
                                .executes(context -> handleChoice(context.getSource(), true)))
                        .then(Commands.literal("no")
                                .executes(context -> handleChoice(context.getSource(), false))))
                .then(Commands.literal("debug")
                        .executes(context -> toggleDebug(context.getSource())))
                .then(Commands.literal("client")
                        .then(Commands.literal("debug")
                                .executes(context -> toggleDebug(context.getSource()))))
                .then(Commands.literal("spawns")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> openScreen(context.getSource()))
                )
                .then(Commands.literal("jammer")
                        .then(Commands.literal("vip")
                                .executes(context -> toggleOwnJammerVip(context.getSource()))
                                .then(Commands.literal("enable")
                                        .executes(context -> setOwnJammerVip(context.getSource(), true)))
                                .then(Commands.literal("disable")
                                        .executes(context -> setOwnJammerVip(context.getSource(), false)))
                                .then(Commands.argument("enabled", BoolArgumentType.bool())
                                        .executes(context -> setOwnJammerVip(context.getSource(), BoolArgumentType.getBool(context, "enabled"))))))
                .then(Commands.literal("admin")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("spawns")
                                .executes(context -> openScreen(context.getSource())))
                        .then(Commands.literal("choice")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.literal("reset")
                                                .executes(context -> resetChoice(context.getSource(), EntityArgument.getPlayers(context, "targets"))))))
                        .then(Commands.literal("starter")
                                .then(Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.literal("reset")
                                                .executes(context -> resetStarter(context.getSource(), EntityArgument.getPlayers(context, "targets"))))))
                        .then(Commands.literal("jammer")
                                .then(Commands.literal("vip")
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.literal("set")
                                                        .executes(context -> setJammerVip(
                                                                context.getSource(),
                                                                EntityArgument.getPlayers(context, "targets"),
                                                                true
                                                        )))
                                                .then(Commands.literal("remove")
                                                        .executes(context -> setJammerVip(
                                                                context.getSource(),
                                                                EntityArgument.getPlayers(context, "targets"),
                                                                false
                                                        )))))))
        );
    }

    private static int handleChoice(CommandSourceStack source, boolean accept) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ChangedExtras.SpecialPlayerData specialData = ChangedExtras.SPECIAL_PLAYERS.get(player.getUUID());
        if (specialData == null) {
            source.sendFailure(Component.literal("§cYou do not have a special transfur starter available."));
            return 0;
        }

        CompoundTag data = player.getPersistentData();
        if (data.getBoolean(ChangedExtras.SPECIAL_CHOICE_MADE_TAG)) {
            source.sendFailure(Component.literal("§cYou have already made your starter transfur choice."));
            return 0;
        }

        data.putBoolean(ChangedExtras.SPECIAL_CHOICE_MADE_TAG, true);

        if (accept) {
            var variant = specialData.variantSupplier().get();
            if (variant != null) {
                ProcessTransfur.setPlayerTransfurVariant(player, variant);
                source.sendSuccess(() -> Component.literal("§aYou have started transfurred as §b" + specialData.displayName() + "§a!"), false);
                if (!player.serverLevel().getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM)) {
                    source.sendSuccess(() -> Component.literal("§e§lWarning: §cThis world doesnt have keep form enabled, whenever you die, you'll lose your form"), false);
                }
            } else {
                source.sendFailure(Component.literal("§cFailed to load transfur variant."));
                return 0;
            }
        } else {
            source.sendSuccess(() -> Component.literal("§eYou have chosen to remain human."), false);
        }

        return 1;
    }

    private static int resetChoice(CommandSourceStack source, Collection<ServerPlayer> targets) {
        int count = 0;
        for (ServerPlayer player : targets) {
            player.getPersistentData().remove(ChangedExtras.SPECIAL_CHOICE_MADE_TAG);
            ChangedExtras.SpecialPlayerData specialData = ChangedExtras.SPECIAL_PLAYERS.get(player.getUUID());
            if (specialData != null) {
                ChangedExtras.sendTransfurPrompt(player, specialData);
            }
            count++;
        }
        int finalCount = count;
        source.sendSuccess(() -> Component.literal("Reset starter transfur choice for " + finalCount + " player(s)."), true);
        return count;
    }

    private static int resetStarter(CommandSourceStack source, Collection<ServerPlayer> targets) {
        int count = 0;
        for (ServerPlayer player : targets) {
            player.getPersistentData().remove(ChangedExtras.RECEIVED_STARTER_KIT_TAG);
            count++;
        }
        int finalCount = count;
        source.sendSuccess(() -> Component.literal("Reset starter kit status for " + finalCount + " player(s)."), true);
        return count;
    }

    private static int toggleDebug(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        boolean enabled = LatexDebugManager.toggle(player);
        source.sendSuccess(() -> Component.literal("§b[ChangedExtras] §fAI debug overlay: " + (enabled ? "§aENABLED" : "§cDISABLED")), false);
        return 1;
    }

    private static int openScreen(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        boolean allowDaySpawns = player.serverLevel().getGameRules().getBoolean(ChangedExtrasGameRules.LATEX_SPAWN_IN_DAY);
        List<LatexSpawnVariantEntry> entries = LatexSpawnRegistry.buildEntries(player.serverLevel().getServer());
        ChangedExtrasNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new OpenLatexSpawnControlScreenPacket(allowDaySpawns, entries));
        return 1;
    }

    private static int toggleOwnJammerVip(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        boolean current = JammerVipManager.isServerVip(player);
        return setOwnJammerVip(source, !current);
    }

    private static int setOwnJammerVip(CommandSourceStack source, boolean vip) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!source.hasPermission(2) && !JammerVipManager.isServerVip(player)) {
            source.sendFailure(Component.literal("§cYou do not have Jammer VIP access. An admin must grant it to you."));
            return 0;
        }

        JammerVipManager.setVip(player, vip);
        source.sendSuccess(() -> Component.literal("§aJammer VIP " + (vip ? "enabled" : "disabled") + "!"), false);
        return 1;
    }

    private static int setJammerVip(CommandSourceStack source, Collection<ServerPlayer> targets, boolean vip) {
        int count = 0;
        for (ServerPlayer player : targets) {
            JammerVipManager.setVip(player, vip);
            count++;
        }

        int finalCount = count;
        source.sendSuccess(() -> Component.literal((vip ? "Granted" : "Revoked") + " Jammer VIP for " + finalCount + " player(s)."), true);
        return count;
    }
}

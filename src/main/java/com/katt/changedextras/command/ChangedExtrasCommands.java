package com.katt.changedextras.command;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.common.debug.LatexDebugManager;
import com.katt.changedextras.common.jammer.JammerVipManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

/**
 * Every Changed Extras command lives under one root node so there is a single
 * place to look for them (plus one short alias):
 *
 * <pre>
 * /changedextras choice [yes|no]   re-show or answer the special starter transfur prompt
 * /changedextras debug             toggle the AI debug overlay
 * /changedextras vip [enable|disable|true|false] [targets]
 *                                 Jammer VIP: bare toggles yours, a value sets yours,
 *                                 and admins may append targets to set others'
 * /changedextras reset &lt;targets&gt;   admin: reset the starter choice and kit, then re-prompt
 * /cedebug                         short alias for /changedextras debug
 * </pre>
 */
@Mod.EventBusSubscriber(modid = ChangedExtras.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
@SuppressWarnings("deprecation")
public final class ChangedExtrasCommands {

    private static final String ROOT = "changedextras";
    private static final String DEBUG_ALIAS = "cedebug";

    private ChangedExtrasCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(root());
        dispatcher.register(Commands.literal(DEBUG_ALIAS)
                .executes(ChangedExtrasCommands::toggleDebug));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> root() {
        return Commands.literal(ROOT)
                .then(choiceNode())
                .then(debugNode())
                .then(vipNode())
                .then(resetNode());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> choiceNode() {
        return Commands.literal("choice")
                .executes(ChangedExtrasCommands::showChoicePrompt)
                .then(Commands.literal("yes")
                        .executes(context -> answerChoice(context.getSource(), true)))
                .then(Commands.literal("no")
                        .executes(context -> answerChoice(context.getSource(), false)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> debugNode() {
        return Commands.literal("debug")
                .executes(ChangedExtrasCommands::toggleDebug);
    }

    private static LiteralArgumentBuilder<CommandSourceStack> vipNode() {
        return Commands.literal("vip")
                .executes(context -> toggleOwnVip(context.getSource()))
                .then(vipValueNode("enable", true))
                .then(vipValueNode("disable", false))
                .then(vipValueNode("true", true))
                .then(vipValueNode("false", false));
    }

    /**
     * Builds {@code /changedextras vip <name> [targets]} for one spelling of the value:
     * it applies to yourself when no targets are given and to the targets (admin only)
     * when they are.
     */
    private static LiteralArgumentBuilder<CommandSourceStack> vipValueNode(String name, boolean vip) {
        return Commands.literal(name)
                .executes(context -> setOwnVip(context.getSource(), vip))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> setVip(context.getSource(), EntityArgument.getPlayers(context, "targets"), vip)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> resetNode() {
        return Commands.literal("reset")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> resetStarter(context.getSource(), EntityArgument.getPlayers(context, "targets"))));
    }

    // ------------------------------------------------------------------ choice

    private static int showChoicePrompt(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        ChangedExtras.SpecialPlayerData specialData = pendingChoice(player, source);
        if (specialData == null) {
            return 0;
        }

        ChangedExtras.sendTransfurPrompt(player, specialData);
        return 1;
    }

    private static int answerChoice(CommandSourceStack source, boolean accept) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        ChangedExtras.SpecialPlayerData specialData = pendingChoice(player, source);
        if (specialData == null) {
            return 0;
        }

        player.getPersistentData().putBoolean(ChangedExtras.SPECIAL_CHOICE_MADE_TAG, true);

        if (accept) {
            var variant = specialData.variantSupplier().get();
            if (variant == null) {
                source.sendFailure(Component.literal("§cFailed to load transfur variant."));
                return 0;
            }

            ProcessTransfur.setPlayerTransfurVariant(player, variant);
            source.sendSuccess(() -> Component.literal("§aYou have started transfurred as §b" + specialData.displayName() + "§a!"), false);
            if (!player.serverLevel().getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM)) {
                source.sendSuccess(() -> Component.literal("§e§lWarning: §cThis world doesnt have keep form enabled, whenever you die, you'll lose your form"), false);
            }
        } else {
            source.sendSuccess(() -> Component.literal("§eYou have chosen to remain human."), false);
        }

        return 1;
    }

    /** Shared guard for every choice command: the player must have a pending special starter choice. */
    private static ChangedExtras.SpecialPlayerData pendingChoice(ServerPlayer player, CommandSourceStack source) {
        ChangedExtras.SpecialPlayerData specialData = ChangedExtras.SPECIAL_PLAYERS.get(player.getUUID());
        if (specialData == null) {
            source.sendFailure(Component.literal("§cYou do not have a special transfur starter available."));
            return null;
        }
        if (player.getPersistentData().getBoolean(ChangedExtras.SPECIAL_CHOICE_MADE_TAG)) {
            source.sendFailure(Component.literal("§cYou have already made your starter transfur choice."));
            return null;
        }
        return specialData;
    }

    private static int resetStarter(CommandSourceStack source, Collection<ServerPlayer> targets) {
        int count = 0;
        for (ServerPlayer player : targets) {
            CompoundTag data = player.getPersistentData();
            data.remove(ChangedExtras.SPECIAL_CHOICE_MADE_TAG);
            data.remove(ChangedExtras.RECEIVED_STARTER_KIT_TAG);
            ChangedExtras.SpecialPlayerData specialData = ChangedExtras.SPECIAL_PLAYERS.get(player.getUUID());
            if (specialData != null) {
                ChangedExtras.sendTransfurPrompt(player, specialData);
            }
            count++;
        }

        int finalCount = count;
        source.sendSuccess(() -> Component.literal("Reset starter choice and kit for " + finalCount + " player(s)."), true);
        return finalCount;
    }

    // ------------------------------------------------------------------ debug

    private static int toggleDebug(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        boolean enabled = LatexDebugManager.toggle(player);
        context.getSource().sendSuccess(() -> Component.literal("§b[ChangedExtras] §fAI debug overlay: " + (enabled ? "§aENABLED" : "§cDISABLED")), false);
        return 1;
    }

    // ------------------------------------------------------------------ jammer vip

    private static int toggleOwnVip(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        return setOwnVip(source, !JammerVipManager.isServerVip(player));
    }

    private static int setOwnVip(CommandSourceStack source, boolean vip) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (!source.hasPermission(2) && !JammerVipManager.isServerVip(player)) {
            source.sendFailure(Component.literal("§cYou do not have Jammer VIP access. An admin must grant it to you."));
            return 0;
        }

        JammerVipManager.setVip(player, vip);
        source.sendSuccess(() -> Component.literal("§aJammer VIP " + (vip ? "enabled" : "disabled") + "!"), false);
        return 1;
    }

    private static int setVip(CommandSourceStack source, Collection<ServerPlayer> targets, boolean vip) {
        if (!source.hasPermission(2)) {
            source.sendFailure(Component.literal("§cOnly admins can set Jammer VIP for other players."));
            return 0;
        }

        int count = 0;
        for (ServerPlayer player : targets) {
            JammerVipManager.setVip(player, vip);
            count++;
        }

        int finalCount = count;
        source.sendSuccess(() -> Component.literal((vip ? "Granted" : "Revoked") + " Jammer VIP for " + finalCount + " player(s)."), true);
        return finalCount;
    }
}

package com.katt.changedextras.compat;

import com.katt.changedextras.entity.beasts.ArtistEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Optional compatibility layer for Changed Addon.
 *
 * <p>Everything in this class is loaded reflectively so Changed Extras can run without the addon.
 * When the addon is present its own implementations are used; otherwise the callers fall back to
 * their local behaviour.</p>
 */
public final class ChangedAddonCompat {
    public static final String MOD_ID = "changed_addon";

    private static final Logger LOGGER = LogManager.getLogger("changedextras");

    private static final String LATEX_SOLVENT_CLASS =
            "net.foxyas.changedaddon.enchantment.LatexSolventEnchantment";
    private static final String LATEX_INFECTION_CLASS =
            "net.foxyas.changedaddon.variant.LatexInfection";
    private static final String HAZARD_BODY_SUIT_CLASS =
            "net.foxyas.changedaddon.item.armor.HazardBodySuit";
    private static final String PAT_EVENT_CLASS =
            "net.foxyas.changedaddon.process.features.ProcessPatFeature$GlobalPatReactionEvent";

    private static Boolean loaded;
    private static boolean latexSolventLookupFailed;
    private static Method latexSolventLevelMethod;
    private static boolean infectionLookupFailed;
    private static Method isPlayerInfectedMethod;
    private static boolean patListenerRegistered;
    private static Class<?> patEventClass;
    private static Field patPatterField;
    private static Field patTargetField;
    private static Field patHandField;

    private ChangedAddonCompat() {
    }

    public static boolean isLoaded() {
        if (loaded == null) {
            try {
                var modList = ModList.get();
                if (modList == null) {
                    // Mod list is not ready yet; don't cache the negative result.
                    return false;
                }
                loaded = modList.isLoaded(MOD_ID);
            } catch (Throwable throwable) {
                LOGGER.debug("changedextras: unable to query mod list for Changed Addon", throwable);
                return false;
            }
        }
        return loaded;
    }

    /**
     * Mirrors {@code LatexSolventEnchantment.getLatexSolventLevelOfEntity(Entity)}.
     */
    public static double getLatexSolventLevel(Entity entity) {
        if (entity == null || !isLoaded()) {
            return 0.0D;
        }
        try {
            if (latexSolventLevelMethod == null && !latexSolventLookupFailed) {
                Class<?> clazz = Class.forName(LATEX_SOLVENT_CLASS);
                latexSolventLevelMethod = clazz.getMethod("getLatexSolventLevelOfEntity", Entity.class);
            }
            if (latexSolventLevelMethod != null) {
                Object value = latexSolventLevelMethod.invoke(null, entity);
                if (value instanceof Number number) {
                    return number.doubleValue();
                }
            }
        } catch (Throwable throwable) {
            latexSolventLookupFailed = true;
            latexSolventLevelMethod = null;
            LOGGER.warn("changedextras: unable to query Changed Addon latex solvent level", throwable);
        }
        return 0.0D;
    }

    /**
     * Mirrors {@code LatexInfection.isPlayerInfected(Player)}.
     */
    public static boolean isPlayerInfected(Player player) {
        if (player == null || !isLoaded()) {
            return false;
        }
        try {
            if (isPlayerInfectedMethod == null && !infectionLookupFailed) {
                Class<?> clazz = Class.forName(LATEX_INFECTION_CLASS);
                isPlayerInfectedMethod = clazz.getMethod("isPlayerInfected", Player.class);
            }
            if (isPlayerInfectedMethod != null) {
                Object value = isPlayerInfectedMethod.invoke(null, player);
                if (value instanceof Boolean infected) {
                    return infected;
                }
            }
        } catch (Throwable throwable) {
            infectionLookupFailed = true;
            isPlayerInfectedMethod = null;
            LOGGER.warn("changedextras: unable to query Changed Addon latex infection state", throwable);
        }
        return false;
    }

    /**
     * Returns true for the addon's Experiment 009 / Experiment 10 boss entities.
     */
    public static boolean isAddonBoss(Mob mob) {
        if (mob == null || !isLoaded()) {
            return false;
        }
        String name = mob.getClass().getName();
        return name.equals("net.foxyas.changedaddon.entity.bosses.Experiment009BossEntity")
                || name.equals("net.foxyas.changedaddon.entity.bosses.Experiment10BossEntity");
    }

    public static boolean isHazardBodySuit(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !isLoaded()) {
            return false;
        }
        return HAZARD_BODY_SUIT_CLASS.equals(stack.getItem().getClass().getName());
    }

    /**
     * Mirrors {@code HazardBodySuit.getBreakSound(ItemStack)} for the addon suit.
     */
    public static SoundEvent getHazardBodySuitBreakSound(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !isHazardBodySuit(stack)) {
            return null;
        }
        try {
            Method method = stack.getItem().getClass().getMethod("getBreakSound", ItemStack.class);
            Object value = method.invoke(stack.getItem(), stack);
            if (value instanceof SoundEvent sound) {
                return sound;
            }
        } catch (Throwable throwable) {
            LOGGER.debug("changedextras: unable to read Changed Addon hazard suit break sound", throwable);
        }
        return null;
    }

    /**
     * Registers a listener for the addon's global pat reaction event. That event is the
     * replacement for implementing the addon's {@code ICustomPatReaction} interface.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerPatListener() {
        if (!isLoaded() || patListenerRegistered) {
            return;
        }
        patListenerRegistered = true;
        try {
            patEventClass = Class.forName(PAT_EVENT_CLASS);
            patPatterField = patEventClass.getField("patter");
            patTargetField = patEventClass.getField("target");
            patHandField = patEventClass.getField("hand");
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, (Class) patEventClass,
                    (Consumer) ChangedAddonCompat::handlePatEvent);
        } catch (Throwable throwable) {
            patEventClass = null;
            LOGGER.warn("changedextras: unable to register Changed Addon pat listener", throwable);
        }
    }

    private static void handlePatEvent(Object event) {
        try {
            if (patEventClass == null || !patEventClass.isInstance(event)) {
                return;
            }
            Object target = patTargetField.get(event);
            if (!(target instanceof ArtistEntity artist)) {
                return;
            }
            Object patter = patPatterField.get(event);
            Object hand = patHandField.get(event);
            if (patter instanceof LivingEntity living && hand instanceof InteractionHand interactionHand) {
                artist.whenPattedReaction(living, interactionHand);
            }
        } catch (Throwable throwable) {
            LOGGER.debug("changedextras: failed to handle Changed Addon pat event", throwable);
        }
    }
}

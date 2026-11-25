package us.kattosaur.changedextras.common.artist;

import us.kattosaur.changedextras.ChangedExtras;
import us.kattosaur.changedextras.entity.ModTransfurVariants;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles the beast's temporary "you are now an average white latex cat" attack.
 *
 * <p>The player's real form (including "human") is snapshotted into persistent
 * data and restored through Changed's own {@link ProcessTransfur} API when the
 * timer expires, when the fight ends, when the player dies, or if another mod
 * changes the form in the meantime. No custom form state is kept on the entity,
 * so keep-form / hardcore / UNTF settings cannot trap somebody as a white cat.</p>
 */
@Mod.EventBusSubscriber(modid = ChangedExtras.MODID)
public final class ArtistTemporaryTransfur {
    /** Empty string means the player was human before the attack. */
    private static final String ORIGINAL_FORM_TAG = "changedextras.temp_transfur.original";
    private static final String EXPIRY_TAG = "changedextras.temp_transfur.expiry";

    private ArtistTemporaryTransfur() {
    }

    /** Applies (or refreshes) the temporary white-cat form. Server-side only. */
    public static void apply(ServerPlayer player, long durationTicks) {
        if (player == null || player.level().isClientSide || player.isSpectator()) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        long expiry = player.serverLevel().getGameTime() + durationTicks;
        if (!isActive(player)) {
            TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
            String original = instance == null ? "" : instance.getParent().getFormId().toString();
            data.putString(ORIGINAL_FORM_TAG, original);
        } else if (isWhiteCat(player)) {
            // Already in the forced form: just extend the timer.
            data.putLong(EXPIRY_TAG, expiry);
            return;
        }
        data.putLong(EXPIRY_TAG, expiry);

        TransfurVariantInstance<?> instance = ProcessTransfur.setPlayerTransfurVariant(player,
                ModTransfurVariants.WHITE_CAT.get(),
                TransfurContext.hazard(TransfurCause.GRAB_REPLICATE));
        if (instance != null) {
            ChangedSounds.broadcastSound(player, ChangedSounds.TRANSFUR_BY_LATEX, 1.0F, 1.0F);
        }
    }

    public static boolean isActive(Player player) {
        return player != null && player.getPersistentData().contains(EXPIRY_TAG);
    }

    /** Restores whatever form the player had before the beast's attack. */
    public static void restore(Player player) {
        if (!isActive(player)) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        String originalId = data.getString(ORIGINAL_FORM_TAG);
        // Clear first: our own variant assignment fires EntityVariantAssigned.
        data.remove(EXPIRY_TAG);
        data.remove(ORIGINAL_FORM_TAG);

        if (originalId.isEmpty()) {
            ProcessTransfur.removePlayerTransfurVariant(player);
            ProcessTransfur.setPlayerTransfurProgress(player, 0.0F);
            return;
        }

        ResourceLocation id = ResourceLocation.tryParse(originalId);
        TransfurVariant<?> variant = id != null && ChangedRegistry.TRANSFUR_VARIANT.get() != null
                ? ChangedRegistry.TRANSFUR_VARIANT.get().getValue(id) : null;
        if (variant != null) {
            ProcessTransfur.setPlayerTransfurVariant(player, variant);
        } else {
            // Unknown/unregistered original: fail safe to human rather than lock the form.
            ProcessTransfur.removePlayerTransfurVariant(player);
        }
        ProcessTransfur.setPlayerTransfurProgress(player, 0.0F);
    }

    /** Called when the boss dies or starts fleeing. */
    public static void restoreAll(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            restore(player);
        }
    }

    private static boolean isWhiteCat(Player player) {
        TransfurVariantInstance<?> instance = ProcessTransfur.getPlayerTransfurVariant(player);
        return instance != null && instance.getParent() == ModTransfurVariants.WHITE_CAT.get();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)
                || player.level().isClientSide || !isActive(player)) {
            return;
        }
        long expiry = player.getPersistentData().getLong(EXPIRY_TAG);
        if (player.serverLevel().getGameTime() >= expiry) {
            restore(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && isActive(player)) {
            // Restore before other handlers snapshot the death form (keep-form / respawn UI).
            restore(player);
        }
    }

    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !isActive(player)) {
            return;
        }
        long expiry = player.getPersistentData().getLong(EXPIRY_TAG);
        if (player.serverLevel().getGameTime() >= expiry || !isWhiteCat(player)) {
            restore(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (isActive(player)) {
            restore(player);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        // Persistent data is not carried over automatically; keep the pending restore.
        CompoundTag oldData = event.getOriginal().getPersistentData();
        CompoundTag newData = event.getEntity().getPersistentData();
        if (oldData.contains(EXPIRY_TAG)) {
            newData.putLong(EXPIRY_TAG, oldData.getLong(EXPIRY_TAG));
            newData.putString(ORIGINAL_FORM_TAG, oldData.getString(ORIGINAL_FORM_TAG));
        }
    }

    @SubscribeEvent
    public static void onVariantAssigned(ProcessTransfur.EntityVariantAssigned event) {
        LivingEntity entity = event.livingEntity;
        if (!(entity instanceof Player player) || !isActive(player)) {
            return;
        }
        if (event.variant != ModTransfurVariants.WHITE_CAT.get()) {
            // Somebody else changed the form (UNTF, syringe, another mod): do not override it.
            player.getPersistentData().remove(EXPIRY_TAG);
            player.getPersistentData().remove(ORIGINAL_FORM_TAG);
        }
    }
}

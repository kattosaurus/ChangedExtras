package com.katt.changedextras;

import com.katt.changedextras.client.renderer.accessory.DyeableClothingRenderer;
import com.katt.changedextras.client.renderer.accessory.JammerHeadphonesRenderer;
import com.katt.changedextras.block.JammerHeadphonesBlock;
import com.katt.changedextras.client.ClientEventHandler;
import com.katt.changedextras.client.particle.JackpotSmokeParticleProvider;
import com.katt.changedextras.common.ChangedExtrasGameRules;
import com.katt.changedextras.common.ChangedExtrasSpawnController;
import com.katt.changedextras.entity.ModEntities;
import com.katt.changedextras.entity.ModEntityAttributes;
import com.katt.changedextras.entity.ModTransfurVariants;
import com.katt.changedextras.init.ChangedExtrasAbilities;
import com.katt.changedextras.init.ChangedExtrasPaintings;
import com.katt.changedextras.init.ChangedExtrasParticles;
import com.katt.changedextras.init.ChangedExtrasSounds;
import com.katt.changedextras.init.ChangedExtrasStructurePieceTypes;
import com.katt.changedextras.init.ChangedExtrasStructureTypes;
import com.katt.changedextras.item.ArtistBrushItem;
import com.katt.changedextras.item.ArtistSketchItem;
import com.katt.changedextras.item.JammerHeadphonesItem;
import com.katt.changedextras.item.LongSleeveShirt;
import com.katt.changedextras.item.PaleTestItem;
import com.katt.changedextras.item.SterileSwabItem;
import com.katt.changedextras.item.UsedVialItem;
import com.katt.changedextras.network.ChangedExtrasNetwork;
import com.katt.changedextras.network.DiscoveryNetwork;
import com.katt.changedextras.network.JackpotStatePacket;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.item.LatexSyringe;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import net.ltxprogrammer.changed.client.renderer.accessory.SimpleClothingRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.AccessoryLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Mod(ChangedExtras.MODID)
public class ChangedExtras {

    private static final String ICECREAM_STREAK_TAG = "changedextras.icecream_streak";
    public static final String RECEIVED_STARTER_KIT_TAG = "changedextras.received_starter_kit";
    public static final String SPECIAL_CHOICE_MADE_TAG = "changedextras.special_choice_made";

    public record SpecialPlayerData(
            Supplier<? extends TransfurVariant<?>> variantSupplier,
            Supplier<? extends Item> syringeSupplier,
            String variantId,
            String displayName,
            Supplier<List<ItemStack>> extraItemsSupplier
    ) {
        public SpecialPlayerData(
                Supplier<? extends TransfurVariant<?>> variantSupplier,
                Supplier<? extends Item> syringeSupplier,
                String variantId,
                String displayName
        ) {
            this(variantSupplier, syringeSupplier, variantId, displayName, List::of);
        }
    }

    public static final UUID SPECIAL_PLAYER_UUID = UUID.fromString("70080b3e-8cf3-46f3-922e-7b3a32269935");
    public static final UUID JAMMER_PLAYER_UUID = UUID.fromString("28a686cf-a2e5-49a0-8420-3c4ca52d6b5c");

    public static final Map<UUID, SpecialPlayerData> SPECIAL_PLAYERS = Map.of(
            SPECIAL_PLAYER_UUID, new SpecialPlayerData(ModTransfurVariants.KATT, () -> ChangedExtras.KATT_SYRINGE.get(), "katt", "Katt"),
            JAMMER_PLAYER_UUID, new SpecialPlayerData(ModTransfurVariants.JAMMER, () -> null, "jammer", "Jammer", () -> List.of(new ItemStack(ChangedExtras.JAMMER_HEADPHONES.get())))
    );

    public static final String MODID = "changedextras";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final int PROTO_BEE_PRIMARY = 0xF5CB42;
    private static final int PROTO_BEE_SECONDARY = 0x4D3029;
    private static final int SNOW_LEOPARD_PRIMARY = 0xA3A3A3;
    private static final int SNOW_LEOPARD_SECONDARY = 0x2E2E2E;
    private static final int TIGER_SHARK_PRIMARY = 0x9AA8AD;
    private static final int TIGER_SHARK_SECONDARY = 0x151C1F;
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Block> ICECREAM_BLOCK =
            BLOCKS.register("icecream_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
    public static final RegistryObject<Item> ICECREAM_BLOCK_ITEM =
            ITEMS.register("icecream_block", () -> new BlockItem(ICECREAM_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Block> JAMMER_HEADPHONES_BLOCK =
            BLOCKS.register("jammer_headphones", () -> new JammerHeadphonesBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .noOcclusion()
                    .strength(0.4F)));
    public static final RegistryObject<Item> ICECREAM_ITEM =
            ITEMS.register("icecream", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEat().nutrition(5).saturationMod(0.6f).build())));

    public static final RegistryObject<LatexSyringe> CONEKAT_MALE_SYRINGE =
            ITEMS.register("conekat_male_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> CONEKAT_FEMALE_SYRINGE =
            ITEMS.register("conekat_female_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> WHITE_CAT_SYRINGE =
            ITEMS.register("white_cat_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> ARTIST_SYRINGE =
            ITEMS.register("artist_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<LatexSyringe> PROTO_BEE_SYRINGE =
            ITEMS.register("proto_bee_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> FURRED_LATEX_TIGER_SHARK_SYRINGE =
            ITEMS.register("furred_latex_tiger_shark_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE_SYRINGE =
            ITEMS.register("fluffed_up_latex_snow_leopard_male_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE_SYRINGE =
            ITEMS.register("fluffed_up_latex_snow_leopard_female_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> THE_PALETTE =
            ITEMS.register("the_palette", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ARTIST_BRUSH =
            ITEMS.register("artist_brush", () -> new ArtistBrushItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> STERILE_SWAB =
            ITEMS.register("sterile_swab", () -> new SterileSwabItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> USED_SWAB =
            ITEMS.register("used_swab", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> VIAL =
            ITEMS.register("vial", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> USED_VIAL =
            ITEMS.register("used_vial", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PROCESSED_VIAL =
            ITEMS.register("processed_vial", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PALE_TEST =
            ITEMS.register("pale_test", () -> new PaleTestItem(new Item.Properties().stacksTo(1)));

    // The Katt Syringe (Usable by everyone)
    public static final RegistryObject<LatexSyringe> KATT_SYRINGE =
            ITEMS.register("katt_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<ForgeSpawnEggItem> CONEKAT_MALE_SPAWN_EGG =
            ITEMS.register("conekat_male_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.CONEKAT_MALE, 0xE3D2BF, 0x5A3A2E, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> CONEKAT_FEMALE_SPAWN_EGG =
            ITEMS.register("conekat_female_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.CONEKAT_FEMALE, 0xE3D2BF, 0xC86A7B, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> WHITE_CAT_SPAWN_EGG =
            ITEMS.register("white_cat_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.WHITE_CAT, 0xF6F3F3, 0xF1CF6E, new Item.Properties()));
    public static final RegistryObject<Item> ARTIST_SPAWN_EGG =
            ITEMS.register("sketch",
                    () -> new ArtistSketchItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> LONG_SLEEVE_SHIRT = ITEMS.register("long_sleeve_shirt",
            () -> new LongSleeveShirt());
    public static final RegistryObject<Item> JAMMER_HEADPHONES = ITEMS.register("jammer_headphones",
            () -> new JammerHeadphonesItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<ForgeSpawnEggItem> KATT_SPAWN_EGG =
            ITEMS.register("katt_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.KATT, 0xFFFFFF, 0xF0F0F0, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> JAMMER_SPAWN_EGG =
            ITEMS.register("jammer_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.JAMMER, 0x36323e, 0x797881, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> PROTO_BEE_SPAWN_EGG =
            ITEMS.register("proto_bee_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.PROTO_BEE, PROTO_BEE_PRIMARY, PROTO_BEE_SECONDARY, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> FURRED_LATEX_TIGER_SHARK_SPAWN_EGG =
            ITEMS.register("furred_latex_tiger_shark_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.FURRED_LATEX_TIGER_SHARK, TIGER_SHARK_PRIMARY, TIGER_SHARK_SECONDARY, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE_SPAWN_EGG =
            ITEMS.register("fluffed_up_latex_snow_leopard_male_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE, SNOW_LEOPARD_PRIMARY, SNOW_LEOPARD_SECONDARY, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE_SPAWN_EGG =
            ITEMS.register("fluffed_up_latex_snow_leopard_female_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE, SNOW_LEOPARD_PRIMARY, SNOW_LEOPARD_SECONDARY, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> ARTIST_MOB_SPAWN_EGG =
            ITEMS.register("artist_spawn_egg",
                    () -> new ForgeSpawnEggItem(ModEntities.ARTIST, 0x5C6BC0, 0xF5F5F5, new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> SYRINGES_TAB =
            CREATIVE_MODE_TABS.register("changedextras_syringes", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.changedextras.changedextras_syringes"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> createVariantSyringeStack(CONEKAT_MALE_SYRINGE.get(), "conekat_male"))
                    .displayItems((parameters, output) -> {
                        output.accept(createVariantSyringeStack(CONEKAT_MALE_SYRINGE.get(), "conekat_male"));
                        output.accept(createVariantSyringeStack(CONEKAT_FEMALE_SYRINGE.get(), "conekat_female"));
                        output.accept(createVariantSyringeStack(WHITE_CAT_SYRINGE.get(), "white_cat"));
                        output.accept(createVariantSyringeStack(ARTIST_SYRINGE.get(), "artist"));
                        output.accept(createVariantSyringeStack(PROTO_BEE_SYRINGE.get(), "proto_bee"));
                        output.accept(createVariantSyringeStack(FURRED_LATEX_TIGER_SHARK_SYRINGE.get(), "furred_latex_tiger_shark"));
                        output.accept(createVariantSyringeStack(FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE_SYRINGE.get(), "fluffed_up_latex_snow_leopard_male"));
                        output.accept(createVariantSyringeStack(FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE_SYRINGE.get(), "fluffed_up_latex_snow_leopard_female"));
                        output.accept(createVariantSyringeStack(KATT_SYRINGE.get(), "katt"));
                        output.accept(KATT_SPAWN_EGG.get());
                        output.accept(JAMMER_SPAWN_EGG.get());
                        output.accept(PROTO_BEE_SPAWN_EGG.get());
                        output.accept(FURRED_LATEX_TIGER_SHARK_SPAWN_EGG.get());
                        output.accept(FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE_SPAWN_EGG.get());
                        output.accept(FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE_SPAWN_EGG.get());
                        output.accept(ARTIST_MOB_SPAWN_EGG.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> MOBS_TAB =
            CREATIVE_MODE_TABS.register("changedextras_mobs", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.changedextras.changedextras_mobs"))
                    .withTabsBefore(SYRINGES_TAB.getKey())
                    .icon(() -> CONEKAT_MALE_SPAWN_EGG.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(CONEKAT_MALE_SPAWN_EGG.get());
                        output.accept(CONEKAT_FEMALE_SPAWN_EGG.get());
                        output.accept(WHITE_CAT_SPAWN_EGG.get());
                        output.accept(ARTIST_SPAWN_EGG.get());
                        output.accept(KATT_SPAWN_EGG.get());
                        output.accept(JAMMER_SPAWN_EGG.get());
                        output.accept(PROTO_BEE_SPAWN_EGG.get());
                        output.accept(FURRED_LATEX_TIGER_SHARK_SPAWN_EGG.get());
                        output.accept(FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE_SPAWN_EGG.get());
                        output.accept(FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE_SPAWN_EGG.get());
                        output.accept(ARTIST_MOB_SPAWN_EGG.get());
                        output.accept(JAMMER_HEADPHONES.get());
                    })
                    .build());

    public ChangedExtras(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        DiscoveryNetwork.bootstrap();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModEntityAttributes::registerEntityAttributes);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        context.registerConfig(ModConfig.Type.SERVER, Config.SPEC);

        ModEntities.REGISTRY.register(modEventBus);
        ModTransfurVariants.REGISTRY.register(modEventBus);
        ChangedExtrasAbilities.REGISTRY.register(modEventBus);
        ChangedExtrasPaintings.REGISTRY.register(modEventBus);
        ChangedExtrasSounds.REGISTRY.register(modEventBus);
        ChangedExtrasParticles.REGISTRY.register(modEventBus);
        ChangedExtrasStructureTypes.REGISTRY.register(modEventBus);
        ChangedExtrasStructurePieceTypes.REGISTRY.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ChangedExtrasGameRules.bootstrap();
        ChangedExtrasNetwork.register();
        event.enqueueWork(ChangedExtras::registerTransfurColors);
        event.enqueueWork(ChangedExtrasSpawnController::registerSpawnPlacements);
        LOGGER.info("[Changed Extras] Loaded in!");
    }

    private static void registerTransfurColors() {
        registerEntityColor("proto_bee", PROTO_BEE_PRIMARY, PROTO_BEE_SECONDARY);
        registerEntityColor("furred_latex_tiger_shark", TIGER_SHARK_PRIMARY, TIGER_SHARK_SECONDARY);
        registerEntityColor("fluffed_up_latex_snow_leopard_male", SNOW_LEOPARD_PRIMARY, SNOW_LEOPARD_SECONDARY);
        registerEntityColor("fluffed_up_latex_snow_leopard_female", SNOW_LEOPARD_PRIMARY, SNOW_LEOPARD_SECONDARY);
    }

    private static void registerEntityColor(String entityId, int primaryColor, int secondaryColor) {
        ChangedEntities.registerEntityColor(ResourceLocation.fromNamespaceAndPath(MODID, entityId), primaryColor, secondaryColor);
    }

    public static ItemStack createVariantSyringeStack(Item syringeItem, String variantId) {
        return Syringe.setPureVariant(
                new ItemStack(syringeItem),
                ResourceLocation.fromNamespaceAndPath(MODID, variantId));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ICECREAM_BLOCK_ITEM.get());
            event.accept(JAMMER_HEADPHONES.get());
        } else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ICECREAM_ITEM.get());
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(THE_PALETTE.get());
            event.accept(STERILE_SWAB.get());
            event.accept(USED_SWAB.get());
            event.accept(VIAL.get());
            event.accept(USED_VIAL.get());
            event.accept(PROCESSED_VIAL.get());
        } else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ARTIST_BRUSH.get());
            event.accept(PALE_TEST.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[Changed Extras] Server starting");
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        SpecialPlayerData specialData = SPECIAL_PLAYERS.get(player.getUUID());
        if (specialData == null) return;

        CompoundTag data = player.getPersistentData();

        // 1. Give starter items on first join (not on respawn)
        if (!data.getBoolean(RECEIVED_STARTER_KIT_TAG)) {
            if (specialData.syringeSupplier() != null && specialData.syringeSupplier().get() != null) {
                ItemStack starterSyringe = createVariantSyringeStack(specialData.syringeSupplier().get(), specialData.variantId());
                if (!player.getInventory().add(starterSyringe)) {
                    player.drop(starterSyringe, false);
                }
            }
            if (specialData.extraItemsSupplier() != null) {
                for (ItemStack extraStack : specialData.extraItemsSupplier().get()) {
                    if (!player.getInventory().add(extraStack.copy())) {
                        player.drop(extraStack.copy(), false);
                    }
                }
            }
            data.putBoolean(RECEIVED_STARTER_KIT_TAG, true);
        }

        // 2. Prompt in chat if they haven't made their choice yet
        if (!data.getBoolean(SPECIAL_CHOICE_MADE_TAG)) {
            sendTransfurPrompt(player, specialData);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag oldData = event.getOriginal().getPersistentData();
        CompoundTag newData = event.getEntity().getPersistentData();
        if (oldData.contains(RECEIVED_STARTER_KIT_TAG)) {
            newData.putBoolean(RECEIVED_STARTER_KIT_TAG, oldData.getBoolean(RECEIVED_STARTER_KIT_TAG));
        }
        if (oldData.contains(SPECIAL_CHOICE_MADE_TAG)) {
            newData.putBoolean(SPECIAL_CHOICE_MADE_TAG, oldData.getBoolean(SPECIAL_CHOICE_MADE_TAG));
        }
    }

    public static void sendTransfurPrompt(ServerPlayer player, SpecialPlayerData specialData) {
        MutableComponent yesBtn = Component.literal("[✔ Yes]")
                .withStyle(style -> style
                        .withColor(ChatFormatting.GREEN)
                        .withBold(true)
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/changedextras choice yes"
                        ))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal("§aClick to start transfurred as " + specialData.displayName())
                        )));

        MutableComponent noBtn = Component.literal("[✖ No]")
                .withStyle(style -> style
                        .withColor(ChatFormatting.RED)
                        .withBold(true)
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/changedextras choice no"
                        ))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal("§cClick to remain human")
                        )));

        player.sendSystemMessage(Component.literal("§6[Changed Extras] §fWould you like to start transfurred as §b" + specialData.displayName() + "§f?"));
        if (!player.serverLevel().getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM)) {
            player.sendSystemMessage(Component.literal("§e§lWarning: §cThis world doesnt have keep form enabled, whenever you die, you'll lose your form"));
        }
        player.sendSystemMessage(Component.literal("  ").append(yesBtn).append(Component.literal("    ")).append(noBtn));
    }

    @SubscribeEvent
    public void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        ItemStack stack = event.getItem();

        // Katt Syringe is now usable by everyone
        if (stack.is(KATT_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "katt"));
        }

        // Standard variant forcing for other syringes
        if (stack.is(WHITE_CAT_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "white_cat"));
        } else if (stack.is(ARTIST_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "artist"));
        } else if (stack.is(PROTO_BEE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "proto_bee"));
        } else if (stack.is(FURRED_LATEX_TIGER_SHARK_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "furred_latex_tiger_shark"));
        } else if (stack.is(FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "fluffed_up_latex_snow_leopard_male"));
        } else if (stack.is(FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "fluffed_up_latex_snow_leopard_female"));
        } else if (stack.is(CONEKAT_MALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "conekat_male"));
        } else if (stack.is(CONEKAT_FEMALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "conekat_female"));
        }
    }

    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof ServerPlayer player)) {
            return;
        }

        ItemStack stack = event.getItem();
        if (!stack.isEdible()) {
            return;
        }

        if (stack.is(ICECREAM_ITEM.get())) {
            int streak = player.getPersistentData().getInt(ICECREAM_STREAK_TAG) + 1;
            if (streak >= 3) {
                player.getPersistentData().putInt(ICECREAM_STREAK_TAG, 0);
                ProcessTransfur.setPlayerTransfurVariant(
                        player,
                        ModTransfurVariants.CONEKATS.getRandomVariant(player.getRandom())
                );
            } else {
                player.getPersistentData().putInt(ICECREAM_STREAK_TAG, streak);
            }
            return;
        }
        player.getPersistentData().putInt(ICECREAM_STREAK_TAG, 0);
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            AccessoryLayer.registerRenderer(
                    ChangedExtras.LONG_SLEEVE_SHIRT.get(),
                    SimpleClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST)
            );
            AccessoryLayer.registerRenderer(
                    ChangedExtras.JAMMER_HEADPHONES.get(),
                    JammerHeadphonesRenderer::new
            );
            event.enqueueWork(() -> ItemProperties.register(
                    ChangedExtras.PALE_TEST.get(),
                    ResourceLocation.fromNamespaceAndPath(MODID, "positive"),
                    (stack, level, entity, seed) -> PaleTestItem.isPositive(stack) ? 1.0F : 0.0F
            ));
            event.enqueueWork(() -> ItemProperties.register(
                    ChangedExtras.PALE_TEST.get(),
                    ResourceLocation.fromNamespaceAndPath(MODID, "negative"),
                    (stack, level, entity, seed) -> PaleTestItem.isNegative(stack) ? 1.0F : 0.0F
            ));

            MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
        }

        @SubscribeEvent
        public static void onRegisterColorHandlers(RegisterColorHandlersEvent.Item event) {
            AccessoryLayer.registerRenderer(
                    ChangedExtras.LONG_SLEEVE_SHIRT.get(),
                    DyeableClothingRenderer.of(ArmorModel.CLOTHING_INNER, EquipmentSlot.CHEST)
            );
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, PROTO_BEE_PRIMARY, PROTO_BEE_SECONDARY),
                    ChangedExtras.PROTO_BEE_SYRINGE.get());
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, TIGER_SHARK_PRIMARY, TIGER_SHARK_SECONDARY),
                    ChangedExtras.FURRED_LATEX_TIGER_SHARK_SYRINGE.get());
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, SNOW_LEOPARD_PRIMARY, SNOW_LEOPARD_SECONDARY),
                    ChangedExtras.FLUFFED_UP_LATEX_SNOW_LEOPARD_MALE_SYRINGE.get(),
                    ChangedExtras.FLUFFED_UP_LATEX_SNOW_LEOPARD_FEMALE_SYRINGE.get());
        }

        private static int syringeLayerColor(int tintIndex, int primaryColor, int secondaryColor) {
            return switch (tintIndex) {
                case 0 -> primaryColor;
                case 1 -> secondaryColor;
                default -> 0xFFFFFF;
            };
        }

        @SubscribeEvent
        public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
            JackpotSmokeParticleProvider.register(event);
        }
    }
}

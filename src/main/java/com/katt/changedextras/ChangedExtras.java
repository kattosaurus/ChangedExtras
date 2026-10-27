package com.katt.changedextras;
import com.katt.changedextras.worldgen.feature.BlueStoneCrystalPatchFeature;
import com.katt.changedextras.worldgen.feature.BlueStonePillarFeature;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import com.katt.changedextras.client.model.catte_bucket;

import com.katt.changedextras.block.*;
import com.katt.changedextras.client.renderer.FeralCatClientRenderer;
import com.katt.changedextras.client.gui.PillBottleScreen;
import com.katt.changedextras.client.renderer.accessory.DyeableClothingRenderer;
import com.katt.changedextras.client.renderer.accessory.JammerHeadphonesRenderer;
import com.katt.changedextras.client.event.ClientEventHandler;
import com.katt.changedextras.client.particle.JackpotSmokeParticleProvider;
import com.katt.changedextras.common.ChangedExtrasGameRules;
import com.katt.changedextras.common.spawn.ChangedExtrasSpawnController;
import com.katt.changedextras.entity.ModEntities;
import com.katt.changedextras.entity.ModEntityAttributes;
import com.katt.changedextras.entity.ModTransfurVariants;
import com.katt.changedextras.entity.beasts.ConeKatFemaleEntity;
import com.katt.changedextras.entity.beasts.ConeKatMaleEntity;
import com.katt.changedextras.entity.beasts.JammerEntity;
import com.katt.changedextras.fluid.ModFluids;
import com.katt.changedextras.init.ChangedExtrasAbilities;
import com.katt.changedextras.init.ChangedExtrasEffects;
import com.katt.changedextras.init.ChangedExtrasMenus;
import com.katt.changedextras.init.ChangedExtrasPaintings;
import com.katt.changedextras.init.ChangedExtrasParticles;
import com.katt.changedextras.init.ChangedExtrasSounds;
import com.katt.changedextras.init.ChangedExtrasStructurePieceTypes;
import com.katt.changedextras.init.ChangedExtrasStructureTypes;
import com.katt.changedextras.init.InitCrTabs;
import com.katt.changedextras.item.ArtistBrushItem;
import com.katt.changedextras.item.ArtistSketchItem;
import com.katt.changedextras.item.CatteBucketArmorItem;
import com.katt.changedextras.item.ChangedExtrasGuideItem;
import com.katt.changedextras.item.JammerHeadphonesItem;
import com.katt.changedextras.item.LongSleeveShirt;
import com.katt.changedextras.item.PaleTestItem;
import com.katt.changedextras.item.PillBottleItem;
import com.katt.changedextras.item.PillItem;
import com.katt.changedextras.item.SterileSwabItem;
import com.katt.changedextras.item.UsedVialItem;
import com.katt.changedextras.network.ChangedExtrasNetwork;
import com.mojang.logging.LogUtils;
import net.ltxprogrammer.changed.entity.variant.GenderedPair;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.ltxprogrammer.changed.init.ChangedTransfurVariants;
import net.ltxprogrammer.changed.item.LatexSyringe;
import net.ltxprogrammer.changed.item.Syringe;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
import org.slf4j.Logger;
import net.ltxprogrammer.changed.client.renderer.accessory.SimpleClothingRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.AccessoryLayer;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Mod(ChangedExtras.MODID)
@SuppressWarnings("deprecation")
public class ChangedExtras {

    public static final Map<RegistryObject<? extends EntityType<? extends Mob>>, RegistryObject<ForgeSpawnEggItem>> SPAWN_EGGS = new LinkedHashMap<>();

    private static final String ICECREAM_STREAK_TAG = "changedextras.icecream_streak";
    private static final String ICECREAM_FLAVOR_TAG = "changedextras.icecream_flavor";
    // How many of the same flavor in a row it takes to transfur. Set to 1 to transfur on the first ice cream.
    private static final int ICECREAM_STREAK_NEEDED = 5;
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
    public static final UUID HAKU_PLAYER_UUID = UUID.fromString("35ac8af6-bde9-4e50-babf-8b6369f5de54");
    public static final UUID THORNIII_PLAYER_UUID = UUID.fromString("4f03fec8-2d22-4fa3-8d54-1e22bb7784ba");

    public static final Map<UUID, SpecialPlayerData> SPECIAL_PLAYERS = Map.of(
            SPECIAL_PLAYER_UUID, new SpecialPlayerData(ModTransfurVariants.KATT, () -> null, "latex_katto", "Katto"),
            JAMMER_PLAYER_UUID, new SpecialPlayerData(ModTransfurVariants.JAMMER, () -> null, "latex_jammer", "Latex Jammer"),
            HAKU_PLAYER_UUID, new SpecialPlayerData(ModTransfurVariants.LATEX_HAKU, () -> null, "latex_haku", "Latex Haku"),
            THORNIII_PLAYER_UUID, new SpecialPlayerData(ModTransfurVariants.LATEX_THORNIII, () -> null, "latex_thorniii", "Latex Thorniii")
    );

    public static final String MODID = "changedextras";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final int PROTO_BEE_PRIMARY = 0xF5CB42;
    private static final int PROTO_BEE_SECONDARY = 0x4D3029;
    private static final int SCP_009_PRIMARY = 0xB51E2B;
    private static final int SCP_009_SECONDARY = 0x4A000E;
    private static final int LATEX_HAZZY_PRIMARY = 0xCD5452;
    private static final int LATEX_HAZZY_SECONDARY = 0xF6F7F7;
    private static final int LATEX_CATTE_PRIMARY = 0xF59842;
    private static final int LATEX_CATTE_SECONDARY = 0x9E5816;
    private static final int LATEX_HAKU_PRIMARY = 0xF0F0F0;
    private static final int LATEX_HAKU_SECONDARY = 0x8B0000;
    private static final int LATEX_THORNIII_PRIMARY = 0x0f0f0f;
    private static final int LATEX_THORNIII_SECONDARY = 0x8dc7ee;
    private static final int LATEX_CARNELINE_PRIMARY = 0x5C458C;
    private static final int LATEX_CARNELINE_SECONDARY = 0xFF075B;
    private static final int LATEX_PANTHER_PRIMARY = 0x14141C;
    private static final int LATEX_PANTHER_SECONDARY = 0xF2C400;
    private static final int MINT_CONEKAT_PRIMARY = 0xA7E6C1;
    private static final int MINT_CONEKAT_SECONDARY = 0x2E8B57;
    private static final int CHOCO_CONEKAT_PRIMARY = 0x8B5A2B;
    private static final int CHOCO_CONEKAT_SECONDARY = 0x3B2415;
    private static final int STRAWBERRY_CONEKAT_PRIMARY = 0xF7A8B8;
    private static final int STRAWBERRY_CONEKAT_SECONDARY = 0xC2185B;
    private static final int NEAPOLITAN_CONEKAT_PRIMARY = 0xF6C6A8;
    private static final int NEAPOLITAN_CONEKAT_SECONDARY = 0x8B5A2B;
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final RegistryObject<Block> DOMAIN_BARRIER = BLOCKS.register("domain_barrier", () -> new DomainBarrierBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).instrument(NoteBlockInstrument.HAT).strength(1800.0F, 3600000.0F).requiresCorrectToolForDrops().lightLevel(state -> 15).sound(SoundType.GLASS).noOcclusion().emissiveRendering((state, level, pos) -> true)));
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
    public static final RegistryObject<Item> DOMAIN_BARRIER_ITEM = ITEMS.register("domain_barrier", () -> new BlockItem(DOMAIN_BARRIER.get(), new Item.Properties()));

    // Pure green (#00FF00) path marker used to lay out scenario routes for the Snep to follow.
    // Right-clicking a route marker toggles it as the start of the route.
    public static final RegistryObject<Block> ROUTE = BLOCKS.register("route", () -> new RouteBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_GREEN)
            .strength(0.25F)
            .sound(SoundType.STONE)
    ));
    public static final RegistryObject<Item> ROUTE_ITEM = ITEMS.register("route", () -> new BlockItem(ROUTE.get(), new Item.Properties()));

    // Pure red (#FF0000) marker: the Snep walks to and stops on this block when the jackpot fails.
    public static final RegistryObject<Block> FAILED_ROUTE = BLOCKS.register("failed_route", () -> new Block(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_RED)
            .strength(0.25F)
            .sound(SoundType.STONE)
    ));
    public static final RegistryObject<Item> FAILED_ROUTE_ITEM = ITEMS.register("failed_route", () -> new BlockItem(FAILED_ROUTE.get(), new Item.Properties()));

    public static final RegistryObject<Block> ICECREAM_BLOCK =
            BLOCKS.register("icecream_block", () -> new Block(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .sound(SoundType.SLIME_BLOCK)
                    )
            );

    public static final RegistryObject<Block> CATTE_BUCKET_BLOCK = BLOCKS.register("catte_bucket",
            () -> new CatteBucketBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> SCP009_CRYSTAL = BLOCKS.register("scp009_crystal",
            () -> new Scp009CrystalBlock(BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.CHIME)
                    .strength(1.5F)
                    .sound(SoundType.AMETHYST)
                    .friction(0.99F)
                    .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<BlockEntityType<Scp009CrystalBlockEntity>> SCP009_CRYSTAL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("scp009_crystal", () -> BlockEntityType.Builder
                    .of(Scp009CrystalBlockEntity::new, SCP009_CRYSTAL.get())
                    .build(null));

    public static final RegistryObject<Block> BLUE_STONE_BLOCK = BLOCKS.register("blue_stone",
            () -> new BlueStoneBlock(BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(1.5F)
                    .sound(SoundType.TUFF)
                    .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> SCP009_CRYSTAL_SMALL = BLOCKS.register("scp009_crystal_small",
            () -> new Scp009CrystalSmallBlock(BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.CHIME)
                    .strength(1.5F)
                    .sound(SoundType.AMETHYST)
                    .friction(0.99F)
                    .noOcclusion()
                    .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Item> SCP009_CRYSTAL_SMALL_ITEM =
            ITEMS.register("scp009_crystal_small", () -> new BlockItem(SCP009_CRYSTAL_SMALL.get(), new Item.Properties()));
    public static final RegistryObject<Item> SCP009_CRYSTAL_ITEM =
            ITEMS.register("scp009_crystal", () -> new BlockItem(SCP009_CRYSTAL.get(), new Item.Properties()));

    public static final RegistryObject<Block> BLUE_STONE_CRYSTAL = BLOCKS.register("blue_stone_crystal",
            () -> new BlueStoneCrystalBlock(BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.CHIME)
                    .strength(0.4F)
                    .sound(SoundType.AMETHYST)
                    .noOcclusion()
                    .lightLevel(state -> 15)
                    .emissiveRendering((state, level, pos) -> true)
                    .hasPostProcess((state, level, pos) -> true)
                    .randomTicks()
            )
    );
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BLUE_STONE_CRYSTAL_PATCH =
            FEATURES.register("blue_stone_crystal_patch", () -> new BlueStoneCrystalPatchFeature(NoneFeatureConfiguration.CODEC));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> BLUE_STONE_PILLAR =
            FEATURES.register("blue_stone_pillar", () -> new BlueStonePillarFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Item> BLUE_STONE_ITEM =
            ITEMS.register("blue_stone", () -> new BlockItem(BLUE_STONE_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLUE_STONE_CRYSTAL_ITEM =
            ITEMS.register("blue_stone_crystal", () -> new BlockItem(BLUE_STONE_CRYSTAL.get(), new Item.Properties()));
    public static final RegistryObject<Item> STRANGE_CRYSTAL_SHARD =
            ITEMS.register("strange_crystal_shard", () -> new Item(new Item.Properties()));

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
    public static final RegistryObject<Item> PINK_ICECREAM_ITEM =
            ITEMS.register("pink_icecream", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEat().nutrition(5).saturationMod(0.6f).build())));
    public static final RegistryObject<Item> MINT_ICECREAM_ITEM =
            ITEMS.register("mint_icecream", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEat().nutrition(5).saturationMod(0.6f).build())));
    public static final RegistryObject<Item> CHOCO_ICECREAM_ITEM =
            ITEMS.register("choco_icecream", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEat().nutrition(5).saturationMod(0.6f).build())));
    public static final RegistryObject<Item> STRAWBERRY_ICECREAM_ITEM =
            ITEMS.register("strawberry_icecream", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEat().nutrition(5).saturationMod(0.6f).build())));
    public static final RegistryObject<Item> NEAPOLITAN_ICECREAM_ITEM =
            ITEMS.register("neapolitan_icecream", () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
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
    public static final RegistryObject<LatexSyringe> SCP_009_SYRINGE =
            ITEMS.register("scp_009_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> LATEX_HAZZY_SYRINGE =
            ITEMS.register("latex_hazzy_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> LATEX_CATTE_SYRINGE =
            ITEMS.register("latex_catte_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> LATEX_HAKU_SYRINGE =
            ITEMS.register("latex_haku_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> LATEX_THORNIII_SYRINGE =
            ITEMS.register("latex_thorniii_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> LATEX_CARNELINE_SYRINGE =
            ITEMS.register("latex_carneline_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> LATEX_PANTHER_SYRINGE =
            ITEMS.register("latex_panther_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> MINT_CONEKAT_MALE_SYRINGE =
            ITEMS.register("mint_conekat_male_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> MINT_CONEKAT_FEMALE_SYRINGE =
            ITEMS.register("mint_conekat_female_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> CHOCO_CONEKAT_MALE_SYRINGE =
            ITEMS.register("choco_conekat_male_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> CHOCO_CONEKAT_FEMALE_SYRINGE =
            ITEMS.register("choco_conekat_female_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> STRAWBERRY_CONEKAT_MALE_SYRINGE =
            ITEMS.register("strawberry_conekat_male_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> STRAWBERRY_CONEKAT_FEMALE_SYRINGE =
            ITEMS.register("strawberry_conekat_female_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> NEAPOLITAN_CONEKAT_MALE_SYRINGE =
            ITEMS.register("neapolitan_conekat_male_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<LatexSyringe> NEAPOLITAN_CONEKAT_FEMALE_SYRINGE =
            ITEMS.register("neapolitan_conekat_female_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> THE_PALETTE =
            ITEMS.register("the_palette", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ARTIST_BRUSH =
            ITEMS.register("artist_brush", () -> new ArtistBrushItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> STERILE_SWAB =
            ITEMS.register("sterile_swab", () -> new SterileSwabItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> USED_SWAB =
            ITEMS.register("used_swab", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> FLASK_OF_TEARS =
            ITEMS.register("flask_of_tears", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CHANGED_EXTRAS_GUIDE =
            ITEMS.register("changed_extras_guide", () -> new ChangedExtrasGuideItem(new Item.Properties()));
    public static final RegistryObject<Item> VIAL =
            ITEMS.register("vial", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> USED_VIAL =
            ITEMS.register("used_vial", () -> new UsedVialItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PROCESSED_VIAL =
            ITEMS.register("processed_vial", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PALE_TEST =
            ITEMS.register("pale_test", () -> new PaleTestItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> CATTE_BUCKET =
            ITEMS.register("catte_bucket", () -> new CatteBucketArmorItem(new Item.Properties().stacksTo(1)));

    // Medicines
    public static final RegistryObject<Item> HYDROXYUREA =
            ITEMS.register("hydroxyurea", () -> new PillItem(
                    new Item.Properties(),
                    12000,
                    "changedextras.last_hydroxyurea",
                    "0.5 Minecraft days (10 minutes)",
                    () -> List.of(
                            new MobEffectInstance(ChangedExtrasEffects.MEDICATED.get(), 12000, 0),
                            new MobEffectInstance(MobEffects.WEAKNESS, 12000, 0)
                    ),
                    List.of("Medicated (10:00) - Slows Pale Virus progress", "Weakness I (10:00)"),
                    () -> List.of(
                            new MobEffectInstance(MobEffects.WEAKNESS, 6000, 2),
                            new MobEffectInstance(MobEffects.POISON, 100, 0)
                    ),
                    List.of("Weakness III (5:00)", "Poison I (0:05)")
            ));

    public static final RegistryObject<Item> CRIZANLIZUMAB =
            ITEMS.register("crizanlizumab", () -> new PillItem(
                    new Item.Properties(),
                    12000,
                    "changedextras.last_crizanlizumab",
                    "0.5 Minecraft days (10 minutes)",
                    () -> List.of(
                            new MobEffectInstance(ChangedExtrasEffects.VASCULAR_FLOW.get(), 12000, 0)
                    ),
                    List.of("Vascular Flow (10:00) - Prevents Pale Virus damage"),
                    () -> List.of(
                            new MobEffectInstance(ChangedExtrasEffects.HYPOXIA.get(), 6000, 0)
                    ),
                    List.of("Hypoxemia (5:00) - Weakness II, Slowness II, 50% Poison damage")
            ));

    public static final RegistryObject<Item> VOXELOTOR =
            ITEMS.register("voxelotor", () -> new PillItem(
                    new Item.Properties(),
                    24000,
                    "changedextras.last_voxelotor",
                    "1.0 Minecraft day (20 minutes)",
                    () -> List.of(
                            new MobEffectInstance(ChangedExtrasEffects.OXYGENATED.get(), 18000, 0)
                    ),
                    List.of("Oxygenated (15:00) - Prevents Pale Virus damage"),
                    () -> List.of(
                            new MobEffectInstance(ChangedExtrasEffects.HYPOXIA.get(), 6000, 0)
                    ),
                    List.of("Hypoxemia (5:00) - Weakness II, Slowness II, 50% Poison damage")
            ));

    public static final RegistryObject<Item> PILL_BOTTLE =
            ITEMS.register("pill_bottle", () -> new PillBottleItem(new Item.Properties()));

    public static final RegistryObject<LatexSyringe> KATT_SYRINGE =
            ITEMS.register("katt_syringe", () -> new LatexSyringe(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<ForgeSpawnEggItem> CONEKAT_MALE_SPAWN_EGG =
            registerSpawnEgg("conekat_male_spawn_egg", ModEntities.CONEKAT_MALE, 0xE3D2BF, 0x5A3A2E);
    public static final RegistryObject<ForgeSpawnEggItem> CONEKAT_FEMALE_SPAWN_EGG =
            registerSpawnEgg("conekat_female_spawn_egg", ModEntities.CONEKAT_FEMALE, 0xE3D2BF, 0xC86A7B);
    public static final RegistryObject<ForgeSpawnEggItem> MINT_CONEKAT_MALE_SPAWN_EGG =
            registerSpawnEgg("mint_conekat_male_spawn_egg", ModEntities.MINT_CONEKAT_MALE, MINT_CONEKAT_PRIMARY, MINT_CONEKAT_SECONDARY);
    public static final RegistryObject<ForgeSpawnEggItem> MINT_CONEKAT_FEMALE_SPAWN_EGG =
            registerSpawnEgg("mint_conekat_female_spawn_egg", ModEntities.MINT_CONEKAT_FEMALE, MINT_CONEKAT_PRIMARY, MINT_CONEKAT_SECONDARY);
    public static final RegistryObject<ForgeSpawnEggItem> CHOCO_CONEKAT_MALE_SPAWN_EGG =
            registerSpawnEgg("choco_conekat_male_spawn_egg", ModEntities.CHOCO_CONEKAT_MALE, CHOCO_CONEKAT_PRIMARY, CHOCO_CONEKAT_SECONDARY);
    public static final RegistryObject<ForgeSpawnEggItem> CHOCO_CONEKAT_FEMALE_SPAWN_EGG =
            registerSpawnEgg("choco_conekat_female_spawn_egg", ModEntities.CHOCO_CONEKAT_FEMALE, CHOCO_CONEKAT_PRIMARY, CHOCO_CONEKAT_SECONDARY);
    public static final RegistryObject<ForgeSpawnEggItem> STRAWBERRY_CONEKAT_MALE_SPAWN_EGG =
            registerSpawnEgg("strawberry_conekat_male_spawn_egg", ModEntities.STRAWBERRY_CONEKAT_MALE, STRAWBERRY_CONEKAT_PRIMARY, STRAWBERRY_CONEKAT_SECONDARY);
    public static final RegistryObject<ForgeSpawnEggItem> STRAWBERRY_CONEKAT_FEMALE_SPAWN_EGG =
            registerSpawnEgg("strawberry_conekat_female_spawn_egg", ModEntities.STRAWBERRY_CONEKAT_FEMALE, STRAWBERRY_CONEKAT_PRIMARY, STRAWBERRY_CONEKAT_SECONDARY);
    public static final RegistryObject<ForgeSpawnEggItem> NEAPOLITAN_CONEKAT_MALE_SPAWN_EGG =
            registerSpawnEgg("neapolitan_conekat_male_spawn_egg", ModEntities.NEAPOLITAN_CONEKAT_MALE, NEAPOLITAN_CONEKAT_PRIMARY, NEAPOLITAN_CONEKAT_SECONDARY);
    public static final RegistryObject<ForgeSpawnEggItem> NEAPOLITAN_CONEKAT_FEMALE_SPAWN_EGG =
            registerSpawnEgg("neapolitan_conekat_female_spawn_egg", ModEntities.NEAPOLITAN_CONEKAT_FEMALE, NEAPOLITAN_CONEKAT_PRIMARY, NEAPOLITAN_CONEKAT_SECONDARY);
    public static final RegistryObject<ForgeSpawnEggItem> WHITE_CAT_SPAWN_EGG =
            registerSpawnEgg("white_cat_spawn_egg", ModEntities.WHITE_CAT, 0xF6F3F3, 0xF1CF6E);
    public static final RegistryObject<ForgeSpawnEggItem> LATEX_THORNIII_SPAWN_EGG =
            registerSpawnEgg("latex_thorniii_spawn_egg", ModEntities.LATEX_THORNIII, LATEX_THORNIII_PRIMARY, LATEX_THORNIII_SECONDARY);
    public static final RegistryObject<Item> ARTIST_SPAWN_EGG =
            ITEMS.register("sketch",
                    () -> new ArtistSketchItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> LONG_SLEEVE_SHIRT = ITEMS.register("long_sleeve_shirt",
            () -> new LongSleeveShirt());
    public static final RegistryObject<Item> JAMMER_HEADPHONES = ITEMS.register("jammer_headphones",
            () -> new JammerHeadphonesItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<ForgeSpawnEggItem> KATT_SPAWN_EGG =
            registerSpawnEgg("katt_spawn_egg", ModEntities.KATT, 0xFFFFFF, 0xF0F0F0);
    public static final RegistryObject<ForgeSpawnEggItem> JAMMER_SPAWN_EGG =
            registerSpawnEgg("jammer_spawn_egg", ModEntities.JAMMER, 0x36323e, 0x797881);
    public static final RegistryObject<ForgeSpawnEggItem> PROTO_BEE_SPAWN_EGG =
            registerSpawnEgg("proto_bee_spawn_egg", ModEntities.PROTO_BEE, PROTO_BEE_PRIMARY, PROTO_BEE_SECONDARY);
    public static final RegistryObject<ForgeSpawnEggItem> ARTIST_MOB_SPAWN_EGG =
            registerSpawnEgg("artist_spawn_egg", ModEntities.ARTIST, 0x5C6BC0, 0xF5F5F5);
    public static final RegistryObject<ForgeSpawnEggItem> SCP_009_SPAWN_EGG =
            registerSpawnEgg("scp_009_spawn_egg", ModEntities.SCP_009, SCP_009_PRIMARY, SCP_009_SECONDARY);
    public static final RegistryObject<ForgeSpawnEggItem> LATEX_HAZZY_SPAWN_EGG =
            registerSpawnEgg("latex_hazzy_spawn_egg", ModEntities.LATEX_HAZZY, LATEX_HAZZY_PRIMARY, LATEX_HAZZY_SECONDARY);

    public static final RegistryObject<ForgeSpawnEggItem> LATEX_CATTE_SPAWN_EGG =
            registerSpawnEgg("latex_catte_spawn_egg", ModEntities.LATEX_CATTE, LATEX_CATTE_PRIMARY, LATEX_CATTE_SECONDARY);
    public static final RegistryObject<ForgeSpawnEggItem> LATEX_HAKU_SPAWN_EGG =
            registerSpawnEgg("latex_haku_spawn_egg", ModEntities.LATEX_HAKU, LATEX_HAKU_PRIMARY, LATEX_HAKU_SECONDARY);

        public static final RegistryObject<ForgeSpawnEggItem> LATEX_CARNELINE_SPAWN_EGG =
                registerSpawnEgg("latex_carneline_spawn_egg", ModEntities.LATEX_CARNELINE, LATEX_CARNELINE_PRIMARY, LATEX_CARNELINE_SECONDARY);
        public static final RegistryObject<ForgeSpawnEggItem> LATEX_PANTHER_SPAWN_EGG =
                registerSpawnEgg("latex_panther_spawn_egg", ModEntities.LATEX_PANTHER, LATEX_PANTHER_PRIMARY, LATEX_PANTHER_SECONDARY);

    private static RegistryObject<ForgeSpawnEggItem> registerSpawnEgg(
            String name,
            RegistryObject<? extends EntityType<? extends Mob>> entityType,
            int primaryColor,
            int secondaryColor
    ) {
        RegistryObject<ForgeSpawnEggItem> spawnEgg = ITEMS.register(name,
                () -> new ForgeSpawnEggItem(entityType, primaryColor, secondaryColor, new Item.Properties()));
        SPAWN_EGGS.put(entityType, spawnEgg);
        return spawnEgg;
    }

    public ChangedExtras(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModEntityAttributes::registerEntityAttributes);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        FEATURES.register(modEventBus);
        ModFluids.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ChangedExtrasEffects.REGISTRY.register(modEventBus);
        ChangedExtrasMenus.REGISTRY.register(modEventBus);
        InitCrTabs.CT_TAB_REGISTRY.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        context.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
        context.registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);

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
        ChangedExtras.LOGGER.info("opposite of male: {}",
                ChangedTransfurVariants.Gendered.getOpposite(ModTransfurVariants.CONEKAT_MALE.get()));
        LOGGER.info("[Changed Extras] Loaded in!");
    }

    private static void registerTransfurColors() {
        registerEntityColor("proto_bee", PROTO_BEE_PRIMARY, PROTO_BEE_SECONDARY);
        registerEntityColor("scp_009", SCP_009_PRIMARY, SCP_009_SECONDARY);
        registerEntityColor("latex_hazzy", LATEX_HAZZY_PRIMARY, LATEX_HAZZY_SECONDARY);
        registerEntityColor("latex_catte", LATEX_CATTE_PRIMARY, LATEX_CATTE_SECONDARY);
        registerEntityColor("latex_haku", LATEX_HAKU_PRIMARY, LATEX_HAKU_SECONDARY);
            registerEntityColor("latex_carneline", LATEX_CARNELINE_PRIMARY, LATEX_CARNELINE_SECONDARY);
            registerEntityColor("latex_panther", LATEX_PANTHER_PRIMARY, LATEX_PANTHER_SECONDARY);
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
            event.accept(SCP009_CRYSTAL_ITEM.get());
            event.accept(BLUE_STONE_CRYSTAL_ITEM.get());
            event.accept(JAMMER_HEADPHONES.get());
            event.accept(ROUTE_ITEM.get());
            event.accept(FAILED_ROUTE_ITEM.get());
        } else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ICECREAM_ITEM.get());
            event.accept(PINK_ICECREAM_ITEM.get());
            event.accept(MINT_ICECREAM_ITEM.get());
            event.accept(CHOCO_ICECREAM_ITEM.get());
            event.accept(STRAWBERRY_ICECREAM_ITEM.get());
            event.accept(NEAPOLITAN_ICECREAM_ITEM.get());
            event.accept(HYDROXYUREA.get());
            event.accept(CRIZANLIZUMAB.get());
            event.accept(VOXELOTOR.get());
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
            event.accept(PILL_BOTTLE.get());
            event.accept(HYDROXYUREA.get());
            event.accept(CRIZANLIZUMAB.get());
            event.accept(VOXELOTOR.get());
            event.accept(CATTE_BUCKET.get());
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
        if (oldData.contains(JammerEntity.VIP_TAG)) {
            newData.putBoolean(JammerEntity.VIP_TAG, oldData.getBoolean(JammerEntity.VIP_TAG));
        }
        if (oldData.contains("changedextras.last_hydroxyurea")) {
            newData.putLong("changedextras.last_hydroxyurea", oldData.getLong("changedextras.last_hydroxyurea"));
        }
        if (oldData.contains("changedextras.last_crizanlizumab")) {
            newData.putLong("changedextras.last_crizanlizumab", oldData.getLong("changedextras.last_crizanlizumab"));
        }
        if (oldData.contains("changedextras.last_voxelotor")) {
            newData.putLong("changedextras.last_voxelotor", oldData.getLong("changedextras.last_voxelotor"));
        }
    }

    public static void sendTransfurPrompt(ServerPlayer player, SpecialPlayerData specialData) {
        MutableComponent yesBtn = Component.literal("[\u2714 Yes]")
                .withStyle(style -> style
                        .withColor(ChatFormatting.GREEN)
                        .withBold(true)
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/changedextras choice yes"
                        ))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal("\u00a7aClick to start transfurred as " + specialData.displayName())
                        )));

        MutableComponent noBtn = Component.literal("[\u2716 No]")
                .withStyle(style -> style
                        .withColor(ChatFormatting.RED)
                        .withBold(true)
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/changedextras choice no"
                        ))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.literal("\u00a7cClick to remain human")
                        )));

        player.sendSystemMessage(Component.literal("\u00a76[Changed Extras] \u00a7fWould you like to start transfurred as \u00a7b" + specialData.displayName() + "\u00a7f?"));
        if (!player.serverLevel().getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM)) {
            player.sendSystemMessage(Component.literal("\u00a7e\u00a7lWarning: \u00a7cThis world doesnt have keep form enabled, whenever you die, you'll lose your form"));
        }
        player.sendSystemMessage(Component.literal("  ").append(yesBtn).append(Component.literal("    ")).append(noBtn));
    }

    @SubscribeEvent
    public void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        ItemStack stack = event.getItem();

        if (stack.is(KATT_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "latex_katto"));
        }

        // Standard variant forcing for other syringes
        if (stack.is(WHITE_CAT_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "latex_white_cat"));
        } else if (stack.is(ARTIST_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "latex_artist"));
        } else if (stack.is(PROTO_BEE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "proto_bee"));
        } else if (stack.is(SCP_009_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "scp_009"));
        } else if (stack.is(LATEX_HAZZY_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "latex_hazzy"));
        } else if (stack.is(LATEX_CATTE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "latex_catte"));
        } else if (stack.is(LATEX_HAKU_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "latex_haku"));
        } else if (stack.is(CONEKAT_MALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "conekat/male"));
        } else if (stack.is(CONEKAT_FEMALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "conekat/female"));
        } else if (stack.is(MINT_CONEKAT_MALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "mint_conekat/male"));
        } else if (stack.is(MINT_CONEKAT_FEMALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "mint_conekat/female"));
        } else if (stack.is(CHOCO_CONEKAT_MALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "choco_conekat/male"));
        } else if (stack.is(CHOCO_CONEKAT_FEMALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "choco_conekat/female"));
        } else if (stack.is(STRAWBERRY_CONEKAT_MALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "strawberry_conekat/male"));
        } else if (stack.is(STRAWBERRY_CONEKAT_FEMALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "strawberry_conekat/female"));
        } else if (stack.is(NEAPOLITAN_CONEKAT_MALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "neapolitan_conekat/male"));
        } else if (stack.is(NEAPOLITAN_CONEKAT_FEMALE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "neapolitan_conekat/female"));
        } else if (stack.is(LATEX_CARNELINE_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "latex_carneline"));
        } else if (stack.is(LATEX_PANTHER_SYRINGE.get())) {
            Syringe.setPureVariant(stack, ResourceLocation.fromNamespaceAndPath(MODID, "latex_panther"));
        }
    }

    /**
     * Returns the conekat pair that matches the given ice cream, or null if the item
     * is not a transfurring ice cream (pink ice cream is intentionally not listed: it is safe).
     */
    private static GenderedPair<ConeKatMaleEntity, ConeKatFemaleEntity> getConekatPairFor(ItemStack stack) {
        if (stack.is(ICECREAM_ITEM.get())) return ModTransfurVariants.CONEKATS;
        if (stack.is(MINT_ICECREAM_ITEM.get())) return ModTransfurVariants.MINT_CONEKATS;
        if (stack.is(CHOCO_ICECREAM_ITEM.get())) return ModTransfurVariants.CHOCO_CONEKATS;
        if (stack.is(STRAWBERRY_ICECREAM_ITEM.get())) return ModTransfurVariants.STRAWBERRY_CONEKATS;
        if (stack.is(NEAPOLITAN_ICECREAM_ITEM.get())) return ModTransfurVariants.NEAPOLITAN_CONEKATS;
        return null;
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

        // Pink ice cream is safe: it never transfurs and doesn't touch the streak
        if (stack.is(PINK_ICECREAM_ITEM.get())) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        GenderedPair<ConeKatMaleEntity, ConeKatFemaleEntity> pair = getConekatPairFor(stack);

        // Not a transfurring ice cream: reset the streak
        if (pair == null) {
            data.putInt(ICECREAM_STREAK_TAG, 0);
            data.remove(ICECREAM_FLAVOR_TAG);
            return;
        }

        // Already transfurred: no progress
        if (ProcessTransfur.isPlayerTransfurred(player)) {
            data.putInt(ICECREAM_STREAK_TAG, 0);
            data.remove(ICECREAM_FLAVOR_TAG);
            return;
        }

        String flavor = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
        int streak = flavor.equals(data.getString(ICECREAM_FLAVOR_TAG))
                ? data.getInt(ICECREAM_STREAK_TAG) + 1
                : 1; // different flavor: start a new streak

        if (streak >= ICECREAM_STREAK_NEEDED) {
            data.putInt(ICECREAM_STREAK_TAG, 0);
            data.remove(ICECREAM_FLAVOR_TAG);
            ProcessTransfur.setPlayerTransfurVariant(player, pair.getRandomVariant(player.getRandom()));
        } else {
            data.putInt(ICECREAM_STREAK_TAG, streak);
            data.putString(ICECREAM_FLAVOR_TAG, flavor);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            FeralCatClientRenderer.register();
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
            event.enqueueWork(() -> MenuScreens.register(
                    ChangedExtrasMenus.PILL_BOTTLE_MENU.get(),
                    PillBottleScreen::new
            ));
            event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(
                    ChangedExtras.BLUE_STONE_CRYSTAL.get(),
                    RenderType.translucent()
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
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, SCP_009_PRIMARY, SCP_009_SECONDARY),
                    ChangedExtras.SCP_009_SYRINGE.get());
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, LATEX_HAZZY_PRIMARY, LATEX_HAZZY_SECONDARY),
                    ChangedExtras.LATEX_HAZZY_SYRINGE.get());
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, LATEX_CATTE_PRIMARY, LATEX_CATTE_SECONDARY),
                    ChangedExtras.LATEX_CATTE_SYRINGE.get());
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, LATEX_HAKU_PRIMARY, LATEX_HAKU_SECONDARY),
                    ChangedExtras.LATEX_HAKU_SYRINGE.get());
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, LATEX_CARNELINE_PRIMARY, LATEX_CARNELINE_SECONDARY),
                    ChangedExtras.LATEX_CARNELINE_SYRINGE.get());
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, LATEX_PANTHER_PRIMARY, LATEX_PANTHER_SECONDARY),
                    ChangedExtras.LATEX_PANTHER_SYRINGE.get());
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, MINT_CONEKAT_PRIMARY, MINT_CONEKAT_SECONDARY),
                    ChangedExtras.MINT_CONEKAT_MALE_SYRINGE.get(),
                    ChangedExtras.MINT_CONEKAT_FEMALE_SYRINGE.get());
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, CHOCO_CONEKAT_PRIMARY, CHOCO_CONEKAT_SECONDARY),
                    ChangedExtras.CHOCO_CONEKAT_MALE_SYRINGE.get(),
                    ChangedExtras.CHOCO_CONEKAT_FEMALE_SYRINGE.get());
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, STRAWBERRY_CONEKAT_PRIMARY, STRAWBERRY_CONEKAT_SECONDARY),
                    ChangedExtras.STRAWBERRY_CONEKAT_MALE_SYRINGE.get(),
                    ChangedExtras.STRAWBERRY_CONEKAT_FEMALE_SYRINGE.get());
            event.register((stack, tintIndex) -> syringeLayerColor(tintIndex, NEAPOLITAN_CONEKAT_PRIMARY, NEAPOLITAN_CONEKAT_SECONDARY),
                    ChangedExtras.NEAPOLITAN_CONEKAT_MALE_SYRINGE.get(),
                    ChangedExtras.NEAPOLITAN_CONEKAT_FEMALE_SYRINGE.get());
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
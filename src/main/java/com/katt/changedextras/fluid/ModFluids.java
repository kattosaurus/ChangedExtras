package com.katt.changedextras.fluid;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.block.Scp009WaterBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ChangedExtras.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, ChangedExtras.MODID);
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS = ChangedExtras.BLOCKS;
    public static final DeferredRegister<Item> ITEMS = ChangedExtras.ITEMS;

    public static final RegistryObject<FluidType> SCP009_WATER_TYPE = FLUID_TYPES.register("scp009_water",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.changedextras.scp009_water")
                    .canSwim(true)
                    .canDrown(true)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .density(1000)
                    .viscosity(1000)
            ) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        @Override
                        public ResourceLocation getStillTexture() {
                            return ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");
                        }

                        @Override
                        public int getTintColor() {
                            return 0x2fff4555; // alpha 0x8C (~55% opacity), red 0xB5, green 0x1E, blue 0x2B
                        }
                    });
                }
            });

    public static final RegistryObject<ForgeFlowingFluid.Source> SCP009_WATER =
            FLUIDS.register("scp009_water", () -> new ForgeFlowingFluid.Source(getProperties()));
    public static final RegistryObject<ForgeFlowingFluid.Flowing> SCP009_WATER_FLOWING =
            FLUIDS.register("scp009_water_flowing", () -> new ForgeFlowingFluid.Flowing(getProperties()));

    public static final RegistryObject<LiquidBlock> SCP009_WATER_BLOCK = BLOCKS.register("scp009_water",
            () -> new Scp009WaterBlock(SCP009_WATER, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .noCollission()
                    .strength(100.0F)
                    .noLootTable()
                    .liquid()));

    public static final RegistryObject<Item> SCP009_WATER_BUCKET = ITEMS.register("scp009_water_bucket",
            () -> new BucketItem(SCP009_WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    private static ForgeFlowingFluid.Properties getProperties() {
        return new ForgeFlowingFluid.Properties(SCP009_WATER_TYPE, SCP009_WATER, SCP009_WATER_FLOWING)
                .slopeFindDistance(4)
                .levelDecreasePerBlock(1)
                .block(SCP009_WATER_BLOCK)
                .bucket(SCP009_WATER_BUCKET);
    }
}

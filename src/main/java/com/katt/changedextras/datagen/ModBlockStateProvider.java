package com.katt.changedextras.datagen;
import com.katt.changedextras.client.model.catte_bucket;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.block.BlueStoneCrystalBlock;
import com.katt.changedextras.block.RouteBlock;
import com.katt.changedextras.block.Scp009CrystalSmallBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ChangedExtras.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerSimpleBlocks();
        registerRoute();
        registerScp009CrystalSmall();
        registerBlueStoneCrystal();
    }

    private void registerSimpleBlocks() {
        simpleBlock(ChangedExtras.FAILED_ROUTE.get());
        simpleBlock(ChangedExtras.SCP009_CRYSTAL.get());

        simpleBlock(ChangedExtras.BLUE_STONE_BLOCK.get(),
                models().withExistingParent("blue_stone", "minecraft:block/cube_all")
                        .texture("all", modLoc("block/blue_stone")));

        simpleBlock(ChangedExtras.ICECREAM_BLOCK.get(),
                models().withExistingParent("icecream_block", "minecraft:block/cube_all")
                        .texture("all", modLoc("block/icecream")));


        simpleBlock(ChangedExtras.DOMAIN_BARRIER.get(), buildDomainBarrierModel());
        simpleBlock(ChangedExtras.CATTE_BUCKET_BLOCK.get(), buildCatteBucketModel());
        simpleBlock(ChangedExtras.JAMMER_HEADPHONES_BLOCK.get(), buildJammerHeadphonesModel());
        simpleBlock(com.katt.changedextras.fluid.ModFluids.SCP009_WATER_BLOCK.get(), buildScp009WaterModel());
    }

    private ModelFile buildDomainBarrierModel() {
        return models().withExistingParent("domain_barrier", "minecraft:block/block")
                .ao(false)
                .texture("particle", modLoc("block/domain_barrier"))
                .texture("all", modLoc("block/domain_barrier"))
                .element().from(0, 0, 0).to(16, 16, 16).shade(false)
                .face(Direction.DOWN).texture("#all").cullface(Direction.DOWN).end()
                .face(Direction.UP).texture("#all").cullface(Direction.UP).end()
                .face(Direction.NORTH).texture("#all").cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).texture("#all").cullface(Direction.SOUTH).end()
                .face(Direction.WEST).texture("#all").cullface(Direction.WEST).end()
                .face(Direction.EAST).texture("#all").cullface(Direction.EAST).end()
                .end();
    }

    private ModelFile buildScp009WaterModel() {
        return models().getBuilder("scp009_water")
                .texture("particle", mcLoc("block/water_still"));
    }

    private void registerRoute() {
        Block block = ChangedExtras.ROUTE.get();
        ModelFile routeModel = models().withExistingParent("route", "minecraft:block/cube_all")
                .texture("all", modLoc("block/route"));
        ModelFile routeStartModel = models().withExistingParent("route_start", "minecraft:block/cube_all")
                .texture("all", modLoc("block/route_start"));

        getVariantBuilder(block)
                .partialState().with(RouteBlock.START, false).addModels(new ConfiguredModel(routeModel))
                .partialState().with(RouteBlock.START, true).addModels(new ConfiguredModel(routeStartModel));
    }

    private void registerScp009CrystalSmall() {
        Block block = ChangedExtras.SCP009_CRYSTAL_SMALL.get();
        ModelFile model = models().withExistingParent("scp009_crystal_small", "block/cross")
                .renderType("minecraft:translucent")
                .texture("cross", modLoc("block/scp009_crystal_small"))
                .texture("particle", modLoc("block/scp009_crystal_small"));

        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(Scp009CrystalSmallBlock.FACING);
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(model);
            switch (facing) {
                case UP -> { /* default: no rotation */ }
                case DOWN -> builder.rotationX(180);
                case NORTH -> builder.rotationX(90);
                case SOUTH -> builder.rotationX(90).rotationY(180);
                case EAST -> builder.rotationX(90).rotationY(90);
                case WEST -> builder.rotationX(90).rotationY(270);
            }
            return builder.build();
        });
    }

    private void registerBlueStoneCrystal() {
        Block block = ChangedExtras.BLUE_STONE_CRYSTAL.get();
        getVariantBuilder(block).forAllStates(state -> {
            int age = state.getValue(BlueStoneCrystalBlock.AGE);
            String size = switch (age) {
                case 0 -> "small";
                case 1 -> "medium";
                default -> "big";
            };
            String color = state.getValue(BlueStoneCrystalBlock.COLOR).getSerializedName();
            ModelFile model = models().getExistingFile(
                    modLoc("block/blue_stone_crystal/" + size + "_" + color));
            Direction facing = state.getValue(BlueStoneCrystalBlock.FACING);
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(model);
            switch (facing) {
                case UP -> { /* default: no rotation */ }
                case DOWN -> builder.rotationX(180);
                case NORTH -> builder.rotationX(90);
                case SOUTH -> builder.rotationX(90).rotationY(180);
                case EAST -> builder.rotationX(90).rotationY(90);
                case WEST -> builder.rotationX(90).rotationY(270);
            }
            return builder.build();
        });
    }

    private ModelFile buildCatteBucketModel() {
        return models().getBuilder("catte_bucket")
                .renderType("minecraft:cutout")
                .texture("0", modLoc("block/catte_bucket"))
                .texture("particle", modLoc("block/catte_bucket"))
                // Lid
                .element().from(3, 7, 2).to(13, 9, 12)
                .rotation().angle(0).axis(Direction.Axis.X).origin(8, 7, 7).end()
                .face(Direction.NORTH).texture("#0").uvs(2.5f, 2.5f, 5, 3).end()
                .face(Direction.EAST).texture("#0").uvs(0, 2.5f, 2.5f, 3).end()
                .face(Direction.SOUTH).texture("#0").uvs(7.5f, 2.5f, 10, 3).end()
                .face(Direction.WEST).texture("#0").uvs(5, 2.5f, 7.5f, 3).end()
                .face(Direction.UP).texture("#0").uvs(5, 2.5f, 2.5f, 0).end()
                .face(Direction.DOWN).texture("#0").uvs(7.5f, 0, 5, 2.5f).end()
                .end()
                // Body
                .element().from(4, 0, 3).to(12, 8, 11)
                .rotation().angle(0).axis(Direction.Axis.X).origin(8, 0, 7).end()
                .face(Direction.NORTH).texture("#0").uvs(2, 5, 4, 7).end()
                .face(Direction.EAST).texture("#0").uvs(0, 5, 2, 7).end()
                .face(Direction.SOUTH).texture("#0").uvs(6, 5, 8, 7).end()
                .face(Direction.WEST).texture("#0").uvs(4, 5, 6, 7).end()
                .face(Direction.UP).texture("#0").uvs(4, 5, 2, 3).end()
                .face(Direction.DOWN).texture("#0").uvs(6, 3, 4, 5).end()
                .end()
                // Handle
                .element().from(3, -1, 1).to(13, 8, 2)
                .rotation().angle(45).axis(Direction.Axis.X).origin(8, 0, 5).end()
                .face(Direction.NORTH).texture("#0").uvs(0.25f, 7.25f, 2.75f, 9.5f).end()
                .face(Direction.EAST).texture("#0").uvs(0, 7.25f, 0.25f, 9.5f).end()
                .face(Direction.SOUTH).texture("#0").uvs(3, 7.25f, 5.5f, 9.5f).end()
                .face(Direction.WEST).texture("#0").uvs(2.75f, 7.25f, 3, 9.5f).end()
                .face(Direction.UP).texture("#0").uvs(2.75f, 7.25f, 0.25f, 7).end()
                .face(Direction.DOWN).texture("#0").uvs(5.25f, 7, 2.75f, 7.25f).end()
                .end();
    }

    private ModelFile buildJammerHeadphonesModel() {
        var builder = models().getBuilder("jammer_headphones")
                .texture("1", modLoc("block/jammer_headphones"))
                .texture("particle", modLoc("block/jammer_headphones"));

        // EarR
        builder.element().from(2, 0, 10).to(4, 4, 14)
                .rotation().angle(0).axis(Direction.Axis.Y).origin(2, 0, 9).end()
                .face(Direction.NORTH).texture("#1").uvs(0.25f, 0.25f, 0.75f, 0.75f).end()
                .face(Direction.EAST).texture("#1").uvs(1.25f, 0.25f, 1.75f, 0.75f).end()
                .face(Direction.SOUTH).texture("#1").uvs(2.25f, 0.25f, 2.75f, 0.75f).end()
                .face(Direction.WEST).texture("#1").uvs(3.25f, 0.25f, 3.75f, 0.75f).end()
                .face(Direction.UP).texture("#1").uvs(4.25f, 0.25f, 4.75f, 0.75f).end()
                .face(Direction.DOWN).texture("#1").uvs(5.25f, 0.25f, 5.75f, 0.75f).end()
                .end()
                // EarRHandle
                .element().from(3, 1, 6).to(4, 3, 10)
                .rotation().angle(0).axis(Direction.Axis.Y).origin(2, 0, 6).end()
                .face(Direction.NORTH).texture("#1").uvs(6.25f, 0.25f, 6.75f, 0.75f).end()
                .face(Direction.EAST).texture("#1").uvs(7.25f, 0.25f, 7.75f, 0.75f).end()
                .face(Direction.SOUTH).texture("#1").uvs(8.25f, 0.25f, 8.75f, 0.75f).end()
                .face(Direction.WEST).texture("#1").uvs(9.25f, 0.25f, 9.75f, 0.75f).end()
                .face(Direction.UP).texture("#1").uvs(10.25f, 0.25f, 10.75f, 0.75f).end()
                .face(Direction.DOWN).texture("#1").uvs(11.25f, 0.25f, 11.75f, 0.75f).end()
                .end()
                // EarLHandle
                .element().from(12, 1, 6).to(13, 3, 10)
                .rotation().angle(0).axis(Direction.Axis.Y).origin(11, 0, 6).end()
                .face(Direction.NORTH).texture("#1").uvs(12.25f, 0.25f, 12.75f, 0.75f).end()
                .face(Direction.EAST).texture("#1").uvs(13.25f, 0.25f, 13.75f, 0.75f).end()
                .face(Direction.SOUTH).texture("#1").uvs(14.25f, 0.25f, 14.75f, 0.75f).end()
                .face(Direction.WEST).texture("#1").uvs(15.25f, 0.25f, 15.75f, 0.75f).end()
                .face(Direction.UP).texture("#1").uvs(0.25f, 1.25f, 0.75f, 1.75f).end()
                .face(Direction.DOWN).texture("#1").uvs(1.25f, 1.25f, 1.75f, 1.75f).end()
                .end()
                // Top
                .element().from(3, 1, 5).to(13, 3, 6)
                .rotation().angle(0).axis(Direction.Axis.Y).origin(10, 0, 2).end()
                .face(Direction.NORTH).texture("#1").uvs(2.25f, 1.25f, 2.75f, 1.75f).end()
                .face(Direction.EAST).texture("#1").uvs(3.25f, 1.25f, 3.75f, 1.75f).end()
                .face(Direction.SOUTH).texture("#1").uvs(4.25f, 1.25f, 4.75f, 1.75f).end()
                .face(Direction.WEST).texture("#1").uvs(5.25f, 1.25f, 5.75f, 1.75f).end()
                .face(Direction.UP).texture("#1").uvs(6.25f, 1.25f, 6.75f, 1.75f).end()
                .face(Direction.DOWN).texture("#1").uvs(7.25f, 1.25f, 7.75f, 1.75f).end()
                .end()
                // EarL
                .element().from(12, 0, 10).to(14, 4, 14)
                .rotation().angle(0).axis(Direction.Axis.Y).origin(12, 0, 9).end()
                .face(Direction.NORTH).texture("#1").uvs(8.25f, 1.25f, 8.75f, 1.75f).end()
                .face(Direction.EAST).texture("#1").uvs(9.25f, 1.25f, 9.75f, 1.75f).end()
                .face(Direction.SOUTH).texture("#1").uvs(10.25f, 1.25f, 10.75f, 1.75f).end()
                .face(Direction.WEST).texture("#1").uvs(11.25f, 1.25f, 11.75f, 1.75f).end()
                .face(Direction.UP).texture("#1").uvs(12.25f, 1.25f, 12.75f, 1.75f).end()
                .face(Direction.DOWN).texture("#1").uvs(13.25f, 1.25f, 13.75f, 1.75f).end()
                .end();

        // Display transforms
        addTransform(builder, net.minecraft.world.item.ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, 158.5f, 0, 0, 0, -8.5f, 0, 1, 1, 1);
        addTransform(builder, net.minecraft.world.item.ItemDisplayContext.THIRD_PERSON_LEFT_HAND, 155.5f, 0, 0, 0, -8.5f, 0.5f, 1, 1, 1);
        addTransform(builder, net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, 86.75f, 0, 0, 2.75f, 0.75f, 3, 1, 1, 1);
        addTransform(builder, net.minecraft.world.item.ItemDisplayContext.FIRST_PERSON_LEFT_HAND, 88.25f, 0, 0, 2.75f, 2.75f, 4.5f, 1, 1, 1);
        addTransform(builder, net.minecraft.world.item.ItemDisplayContext.GROUND, 0, 0, 0, 0, 2.75f, 0, 0.5f, 0.5f, 0.5f);
        addTransform(builder, net.minecraft.world.item.ItemDisplayContext.GUI, 90, 0, 0, 0, -2, 0, 1, 1, 1);
        addTransform(builder, net.minecraft.world.item.ItemDisplayContext.HEAD, -90, 0, 0, 0, -2.5f, -7, 0.85f, 0.85f, 0.85f);
        addTransform(builder, net.minecraft.world.item.ItemDisplayContext.FIXED, 90, 0, 0, 0, 1.25f, 6.5f, 1, 1, 1);
        return builder;
    }

    private static void addTransform(net.minecraftforge.client.model.generators.ModelBuilder<?> builder,
                                     net.minecraft.world.item.ItemDisplayContext context,
                                     float rx, float ry, float rz, float tx, float ty, float tz,
                                     float sx, float sy, float sz) {
        builder.transforms().transform(context)
                .rotation(rx, ry, rz).translation(tx, ty, tz).scale(sx, sy, sz).end();
    }
}

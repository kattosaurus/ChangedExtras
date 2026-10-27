package com.katt.changedextras.block;

import com.katt.changedextras.ChangedExtras;
import com.katt.changedextras.common.latex.CarnelineTransfurHandler;
import com.katt.changedextras.common.latex.PantherTransfurHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class BlueStoneCrystalBlock extends DirectionalBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 2);
    public static final EnumProperty<CrystalColor> COLOR = EnumProperty.create("color", CrystalColor.class);

    private static final int MAX_AGE = 2;
    private static final int GROWTH_CHANCE_DENOMINATOR = 10;
    private static final double MEDIUM_MIN_DISTANCE = 3.0D;
    private static final double BIG_MIN_DISTANCE = 6.0D;

    public BlueStoneCrystalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AGE, 0)
                .setValue(COLOR, CrystalColor.WHITE)
                .setValue(FACING, Direction.UP));
    }

    public static CrystalColor colorForLight(int lightLevel) {
        if (lightLevel > 13) {
            return CrystalColor.BLUE;
        }
        if (lightLevel > 7) {
            return CrystalColor.RED;
        }
        return CrystalColor.WHITE;
    }

    public static BlockState createState(Level level, BlockPos pos, Direction facing) {
        return ChangedExtras.BLUE_STONE_CRYSTAL.get()
                .defaultBlockState()
                .setValue(FACING, facing)
                .setValue(COLOR, colorForLight(level.getMaxLocalRawBrightness(pos)));
    }

    public static boolean canGrowToMedium(LevelReader level, BlockPos pos) {
        int radius = (int) Math.ceil(MEDIUM_MIN_DISTANCE);
        for (BlockPos target : BlockPos.betweenClosed(
                pos.offset(-radius, -radius, -radius),
                pos.offset(radius, radius, radius))) {
            if (target.equals(pos)) {
                continue;
            }
            if (pos.closerThan(target, MEDIUM_MIN_DISTANCE)) {
                BlockState targetState = level.getBlockState(target);
                if (targetState.is(ChangedExtras.BLUE_STONE_CRYSTAL.get()) && targetState.getValue(AGE) >= 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean canGrowToBig(LevelReader level, BlockPos pos) {
        int radius = (int) Math.ceil(BIG_MIN_DISTANCE);
        for (BlockPos target : BlockPos.betweenClosed(
                pos.offset(-radius, -radius, -radius),
                pos.offset(radius, radius, radius))) {
            if (target.equals(pos)) {
                continue;
            }
            if (pos.closerThan(target, BIG_MIN_DISTANCE)) {
                BlockState targetState = level.getBlockState(target);
                if (targetState.is(ChangedExtras.BLUE_STONE_CRYSTAL.get()) && targetState.getValue(AGE) >= 1) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState grownState = state;
        CrystalColor color = colorForLight(level.getMaxLocalRawBrightness(pos));
        if (grownState.getValue(COLOR) != color) {
            grownState = grownState.setValue(COLOR, color);
        }

        int age = grownState.getValue(AGE);
        if (random.nextInt(GROWTH_CHANCE_DENOMINATOR) == 0) {
            if (age == 0 && canGrowToMedium(level, pos)) {
                grownState = grownState.setValue(AGE, 1);
            } else if (age == 1 && canGrowToBig(level, pos)) {
                grownState = grownState.setValue(AGE, 2);
            }
        }

        if (grownState != state) {
            level.setBlock(pos, grownState, 3);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        int age = state.getValue(AGE);
        if (age == 0) {
            return canGrowToMedium(level, pos);
        } else if (age == 1) {
            return canGrowToBig(level, pos);
        }
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        if (age == 0 && canGrowToMedium(level, pos)) {
            level.setBlock(pos, state.setValue(AGE, 1), 3);
        } else if (age == 1 && canGrowToBig(level, pos)) {
            level.setBlock(pos, state.setValue(AGE, 2), 3);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide) {
            if (state.getValue(COLOR) == CrystalColor.RED) {
                CarnelineTransfurHandler.onTouchedRedCrystal(entity);
            } else if (state.getValue(COLOR) == CrystalColor.WHITE && state.getValue(AGE) > 0) {
                PantherTransfurHandler.onTouchedWhiteCrystal(entity, state);
            }
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        super.stepOn(level, pos, state, entity);
        if (!level.isClientSide) {
            if (state.getValue(COLOR) == CrystalColor.RED) {
                CarnelineTransfurHandler.onTouchedRedCrystal(entity);
            } else if (state.getValue(COLOR) == CrystalColor.WHITE && state.getValue(AGE) > 0) {
                PantherTransfurHandler.onTouchedWhiteCrystal(entity, state);
            }
        }
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);
        if (!level.isClientSide) {
            if (state.getValue(COLOR) == CrystalColor.RED) {
                CarnelineTransfurHandler.onTouchedRedCrystal(player);
            } else if (state.getValue(COLOR) == CrystalColor.WHITE && state.getValue(AGE) > 0) {
                PantherTransfurHandler.onTouchedWhiteCrystal(player, state);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return switch (state.getValue(AGE)) {
            case 0 -> shapeFor(facing, 6.0D, 10.0D, 3.0D);
            case 1 -> shapeFor(facing, 4.0D, 12.0D, 13.0D);
            default -> Shapes.block();
        };
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        BlockState supportState = level.getBlockState(supportPos);
        return supportState.is(ChangedExtras.BLUE_STONE_BLOCK.get()) && supportState.isFaceSturdy(level, supportPos, facing);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction == state.getValue(FACING).getOpposite() && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 2;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState placedState = this.defaultBlockState()
                .setValue(FACING, context.getClickedFace())
                .setValue(COLOR, colorForLight(level.getMaxLocalRawBrightness(pos)));
        return placedState.canSurvive(level, pos) ? placedState : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, COLOR, FACING);
    }

    private static VoxelShape shapeFor(Direction facing, double min, double max, double height) {
        return switch (facing) {
            case UP -> Block.box(min, 0.0D, min, max, height, max);
            case DOWN -> Block.box(min, 16.0D - height, min, max, 16.0D, max);
            case NORTH -> Block.box(min, min, 0.0D, max, max, height);
            case SOUTH -> Block.box(min, min, 16.0D - height, max, max, 16.0D);
            case EAST -> Block.box(0.0D, min, min, height, max, max);
            case WEST -> Block.box(16.0D - height, min, min, 16.0D, max, max);
        };
    }

    public enum CrystalColor implements StringRepresentable {
        BLUE,
        RED,
        WHITE;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}

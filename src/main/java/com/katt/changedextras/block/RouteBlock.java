package com.katt.changedextras.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Green path marker used to lay out scenario routes for the Snep to follow.
 * Right-clicking the block toggles {@link #START}, marking this marker as the first point of the
 * route (the Snep spawns and starts walking from the start marker).
 */
public class RouteBlock extends Block {
    public static final BooleanProperty START = BooleanProperty.create("start");

    public RouteBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(START, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(START);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            boolean starting = !state.getValue(START);
            level.setBlock(pos, state.setValue(START, starting), 3);
            player.displayClientMessage(
                    Component.translatable(starting
                            ? "message.changedextras.route.start_set"
                            : "message.changedextras.route.start_cleared"), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}

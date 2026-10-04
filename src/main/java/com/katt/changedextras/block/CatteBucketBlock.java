package com.katt.changedextras.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("deprecation")
public class CatteBucketBlock extends Block {
    // Usamos Shapes.box en lugar de VoxelShapes.box
    private static final VoxelShape SHAPE = Shapes.box(
            0.125D,  // X inicial (centrado para un ancho exacto de 0.75)
            0.000D,  // Y inicial (desde el suelo)
            0.125D,  // Z inicial (empujado 2 píxeles/0.125 del lado negativo hacia adelante)
            0.875D,  // X final (0.125 + 0.75 = 0.875)
            0.600D,  // Y final (mantiene el alto exacto de 0.9 bloques)
            0.900D   // Z final (mantiene el límite del eje Z positivo)
    );

    public CatteBucketBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}

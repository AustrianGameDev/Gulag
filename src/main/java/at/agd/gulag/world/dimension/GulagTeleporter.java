package at.agd.gulag.world.dimension;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class GulagTeleporter implements ITeleporter
{
    public static BlockPos thisPos;
    public static boolean insideDimension = true;

    public GulagTeleporter(BlockPos pos, boolean insideDim)
    {
        thisPos = pos;
        insideDimension = insideDim;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
    {
        entity = repositionEntity.apply(false);
        double y = 61;

        if(insideDimension)
        {
            y = thisPos.getY();
        }

        BlockPos destinationPos = new BlockPos(thisPos.getX(), y, thisPos.getZ());

        int tries = 0;
        while((destWorld.getBlockState(destinationPos).getMaterial() != Material.AIR) &&
            !destWorld.getBlockState(destinationPos).isReplaceable(Fluids.WATER) &&
            destWorld.getBlockState(destinationPos.up()).getMaterial() != Material.AIR &&
            !destWorld.getBlockState(destinationPos.up()).isReplaceable(Fluids.WATER) && tries < 25)
        {
            destinationPos = destinationPos.up(2);
            tries++;
        }

        entity.setPositionAndUpdate(destinationPos.getX(), destinationPos.getY(), destinationPos.getZ());

        return entity;
    }
}

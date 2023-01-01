package at.agd.gulag;

import at.agd.gulag.entity.EnemyBuilder;
import at.agd.gulag.reference.Values;
import at.agd.gulag.world.dimension.GulagDimension;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;
import java.util.UUID;

public class GulagSpawner
{
    // Spawns the enemy the player must fight
    // Returns the enemy's uniqueId to keep track of it
    public static UUID spawnEntity(PlayerEntity player, int enemyUuidCount)
    {
        MinecraftServer server = player.getServer();
        ServerWorld serverWorld = server.getWorld(GulagDimension.GulagDim);

        BlockPos pos = Values.Positions.gulagEnemyPos;
        Entity entity = getRandomEntity(serverWorld);
        entity.setUniqueId(new UUID(0, enemyUuidCount));

        entity = EnemyBuilder.buildEnemy(entity);

        serverWorld.addEntity(entity);
        entity.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());

        return entity.getUniqueID();

        //System.out.println(serverWorld.getWorldInfo().getDifficulty());
        //System.out.println(serverWorld.getDifficultyForLocation(player.getPosition()).getAdditionalDifficulty());
    }

    private static Entity getRandomEntity(ServerWorld serverWorld)
    {
        Random rand = new Random();
        EntityType[] entityTypes = {EntityType.ZOMBIE, EntityType.SKELETON, EntityType.STRAY};
        return entityTypes[rand.nextInt(entityTypes.length)].create(serverWorld);

        // ToDo: Add more enemies
    }

    public static void removeEntity(PlayerEntity player, UUID entityUUID)
    {
        MinecraftServer server = player.getServer();
        ServerWorld serverWorld = server.getWorld(GulagDimension.GulagDim);
        Entity entity = serverWorld.getEntityByUuid(entityUUID);
        serverWorld.removeEntity(entity, false);
    }
}

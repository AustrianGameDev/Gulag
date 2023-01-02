package at.agd.gulag.events;

import at.agd.gulag.Gulag;
import at.agd.gulag.GulagController;
import at.agd.gulag.commands.GulagLeaveCommand;
import at.agd.gulag.commands.GulagVisitCommand;
import at.agd.gulag.db.PlayerPosInfoDB;
import at.agd.gulag.pojo.PlayerPosInfo;
import at.agd.gulag.util.Util;
import at.agd.gulag.world.dimension.GulagTeleportHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.event.DifficultyChangeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.server.command.ConfigCommand;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Gulag.MODID)
public class GulagEventHandler
{
    // ToDo: No items on the ground in the gulag

    private static GulagController gulagController = GulagController.getInstance();

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        // Set the difficulty
        World playerWorld = event.getPlayer().getEntityWorld();
        gulagController.setDifficulty(playerWorld.getDifficulty(), playerWorld.getWorldInfo().isHardcore());

        // Build Gulag dimension
        try
        {
            // Get the name of the save to make a path to it
            ServerWorld world = (ServerWorld)playerWorld;
            Field field = ObfuscationReflectionHelper.<ServerWorld>findField(
                    (Class<? super ServerWorld>) world.getClass(), "field_241103_E_");
            field.setAccessible(true);
            IServerWorldInfo swi = (IServerWorldInfo)field.get(world);
            String worldName = swi.getWorldName(); // Might switch from Reflection to RegEx -> world.toString();

            // Check if region folder of the Gulag exists:
            //      If it exists: Do nothing
            //      If it does not exist: Create the region folder and copy the files to it
            Path homeDir = Paths.get(System.getProperty("user.dir"));
            Path saveDir = Paths.get(homeDir.toString(), "saves");
            Path gulagRegionDir =
                    Paths.get(saveDir.toString(), worldName, "dimensions", "gulag", "gulag_dim", "region");

            File regionDir = new File(gulagRegionDir.toString());
            if(regionDir.exists())
            {
                // The Gulag does already exist so there is no need to copy it again
                return;
            }

            // Create the region folder and copy the files to it
            regionDir.mkdir();

            InputStream is;
            String[] regionFiles = {"r.0.0.mca", "r.0.-1.mca", "r.-1.0.mca", "r.-1.-1.mca"};

            for(String file : regionFiles)
            {
                is = Util.getInputStream("/region/" + file);
                Files.copy(is, Paths.get(gulagRegionDir.toString(), file));
            }
        }
        catch (Exception e)
        {
        }
    }

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event)
    {
        new GulagVisitCommand(event.getDispatcher());
        new GulagLeaveCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onDifficultyChangeEvent(DifficultyChangeEvent event)
    {
        gulagController.setDifficulty(event.getDifficulty());
    }

    @SubscribeEvent
    public static void onPlayerDeathEvent(LivingDeathEvent event)
    {
        Entity entity = event.getEntity();

        if(entity.getEntityWorld().isRemote)
        {
            return;
        }

        if(entity.getType().toString().equals("entity.minecraft.player"))
        {
            // Save the position where the player died alongside ID and UUID
            PlayerPosInfoDB pidb = PlayerPosInfoDB.getInstance();
            int id = entity.getEntityId();

            if(!pidb.exists(id))
            {
                UUID uuid = entity.getUniqueID();
                BlockPos pos = entity.getPosition();

                // ToDo: Check dimension where player died -> On Gulag Win player always respawns in the overworld
                // DimensionKey in PlayerPosInfo
                PlayerPosInfo ppi = new PlayerPosInfo(id, uuid, pos);
                pidb.addPlayerPosInfo(ppi);
            }
            else // Player died in the Gulag -> Info is not needed anymore
            {
                pidb.removePlayerPosInfoById(id);
                gulagController.playerDiedInGulag(event);
            }
        }
        else
        {
            gulagController.checkIfEnemyDied(event);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event)
    {
        PlayerEntity player = event.getPlayer();
        GulagController.onPlayerIntoGulag(player);
    }

    @SubscribeEvent
    public static void onGulagWinEvent(GulagEvent.GulagWinEvent event)
    {
        GulagTeleportHelper.onGulagWinEvent(event);

        PlayerEntity player = event.getPlayer();
        PlayerPosInfoDB.getInstance().removePlayerPosInfoById(player.getEntityId());
        //player.getEntityWorld().playSound(player, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP,
        //        SoundCategory.MUSIC, 1.0f, 0.0f);
    }

    @SubscribeEvent
    public static void onChunkLoadEvent(ChunkEvent.Load event)
    {
        // 0.0001 -> Value set in /data/gulag/dimension_type/gulag_dim.json
        if(event.getWorld().getDimensionType().getCoordinateScale() == 0.0001)
        {
            //gulagController.checkForEnemyToRemove();
        }
    }

    @SubscribeEvent
    public static void test(PlayerEvent.ItemPickupEvent event)
    {
        //GulagEventHooks.gulagWinEvent(event.getPlayer());

        //System.out.println(event.getEntity().getEntityWorld().getDifficulty());
        //System.out.println(event.getEntity().getEntityWorld().getWorldInfo().isHardcore());
    }
}

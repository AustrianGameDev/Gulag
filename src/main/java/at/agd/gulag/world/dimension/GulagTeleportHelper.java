package at.agd.gulag.world.dimension;

import at.agd.gulag.db.PlayerPosInfoDB;
import at.agd.gulag.events.GulagEvent;
import at.agd.gulag.pojo.PlayerPosInfo;
import at.agd.gulag.reference.Values;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class GulagTeleportHelper
{
    public static void onPlayerRespawn(PlayerEntity player)
    {
        PlayerPosInfoDB pidb = PlayerPosInfoDB.getInstance();
        World world = player.getEntityWorld();

        if(!world.isRemote)
        {
            if(!player.isCrouching())
            {
                MinecraftServer server = world.getServer();

                if(server != null)
                {
                    int playerID = player.getEntityId();

                    //if(world.getDimensionKey() != GulagDimension.GulagDim)
                    if(pidb.exists(playerID)) // Teleport to Gulag
                    {
                        ServerWorld gulagDim = server.getWorld(GulagDimension.GulagDim);

                        if(gulagDim != null)
                        {
                            player.changeDimension(gulagDim, new GulagTeleporter(Values.Positions.gulagFightPos, true));
                        }
                    }
                    else // When you die in the Gulag -> Teleport to Overworld
                    {
                        ServerWorld overworldDim = server.getWorld(World.OVERWORLD);

                        if(overworldDim != null)
                        {
                            // ToDo: Does not return bed position even if bed is present, also in GulagLeaveCommand
                            BlockPos pos = player.getBedPosition().orElse(overworldDim.getSpawnPoint());

                            // ToDo: It does not add the values, also in GulagLeaveCommand
                            pos.add(0.5, 0.0, 0.5);
                            player.changeDimension(overworldDim, new GulagTeleporter(pos, false));
                        }
                    }
                }
            }
        }
    }

    public static void onGulagWinEvent(GulagEvent.GulagWinEvent event)
    {
        PlayerPosInfoDB pidb = PlayerPosInfoDB.getInstance();
        PlayerEntity player = event.getPlayer();
        World world = player.getEntityWorld();

        if(!world.isRemote)
        {
            if(!player.isCrouching())
            {
                MinecraftServer server = world.getServer();

                if(server != null)
                {
                    int playerID = player.getEntityId();

                    if(pidb.exists(playerID))
                    {
                        PlayerPosInfo ppi = pidb.getPlayerInfoById(playerID);
                        ServerWorld overworldDim = server.getWorld(World.OVERWORLD);
                        BlockPos pos = ppi.getPosition();

                        pos.add(0.5, 0.0, 0.5);
                        if(overworldDim != null)
                        {
                            player.changeDimension(overworldDim, new GulagTeleporter(pos, false));
                            pidb.removePlayerPosInfoById(playerID);
                        }
                    }
                }
            }
        }
    }

    public static void toWaitingArea(PlayerEntity player)
    {
        World world = player.getEntityWorld();

        if(!world.isRemote)
        {
            if(!player.isCrouching())
            {
                MinecraftServer server = world.getServer();

                if(server != null)
                {
                    ServerWorld gulagDim = server.getWorld(GulagDimension.GulagDim);
                    BlockPos pos = Values.Positions.gulagVisitPos;

                    player.changeDimension(gulagDim, new GulagTeleporter(pos, true));
                }
            }
        }
    }

    public static int visitGulag(CommandSource source) throws CommandSyntaxException
    {
        ServerPlayerEntity player = source.asPlayer();
        PlayerPosInfoDB pidb = PlayerPosInfoDB.getInstance();

        if(pidb.exists(player.getEntityId()))
        {
            source.sendFeedback(new StringTextComponent("Player fighting in the Gulag cannot reenter in!"), true);
            return -1;
        }

        World world = player.getEntityWorld();

        if(!world.isRemote)
        {
            if(!player.isCrouching())
            {
                MinecraftServer server = world.getServer();

                if(server != null)
                {
                    ServerWorld gulagDim = server.getWorld(GulagDimension.GulagDim);
                    BlockPos pos = Values.Positions.gulagVisitPos;

                    // ToDo: Fix rotation, also in toWaitingArea
                    player.changeDimension(gulagDim, new GulagTeleporter(pos, true));
                    source.sendFeedback(new StringTextComponent("Player teleported to Gulag!"), true);
                    return 1;
                }
            }
        }

        //float yawn = Values.Rotations.gulagVisitYawn;
        //int pitch = Values.Rotations.gulagVisitPitch;
        ////player.setPositionAndRotationDirect(pos.getX(), pos.getY(), pos.getZ(), yawn, pitch, 1, true);
        //player.setPositionAndUpdate(pos.getX(), pos.getY(), pos.getZ());
        //player.setHeadRotation(yawn, pitch);
        source.sendFeedback(new StringTextComponent("Something went wrong!"), true);
        return -1;
    }

    public static int leaveGulag(CommandSource source) throws CommandSyntaxException
    {
        ServerPlayerEntity player = source.asPlayer();
        PlayerPosInfoDB pidb = PlayerPosInfoDB.getInstance();

        if(pidb.exists(player.getEntityId()))
        {
            source.sendFeedback(new StringTextComponent("Player fighting in the Gulag cannot leave!"), true);
            return -1;
        }

        World world = player.getEntityWorld();

        if(!world.isRemote)
        {
            if(!player.isCrouching())
            {
                MinecraftServer server = player.getServer();

                if(server != null)
                {
                    ServerWorld overworldDim = server.getWorld(World.OVERWORLD);

                    if(overworldDim != null)
                    {
                        BlockPos pos = player.getBedPosition().orElse(overworldDim.getSpawnPoint());

                        pos.add(0.5, 0.0, 0.5);
                        player.changeDimension(overworldDim, new GulagTeleporter(pos, false));
                        source.sendFeedback(new StringTextComponent("Player left Gulag!"), true);
                        return 1;
                    }
                }
            }
        }
        source.sendFeedback(new StringTextComponent("Something went wrong!"), true);
        return -1;
    }
}

package at.agd.gulag.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;

public class GulagEventHooks
{
    public static void fireGulagWinEvent(PlayerEntity player)
    {
        MinecraftForge.EVENT_BUS.post(new GulagEvent.GulagWinEvent(player));
    }
}

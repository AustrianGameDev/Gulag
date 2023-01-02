package at.agd.gulag.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class GulagEvent extends PlayerEvent
{
    public GulagEvent(PlayerEntity player)
    {
        super(player);
    }

    public static class GulagWinEvent extends GulagEvent
    {
        public GulagWinEvent(PlayerEntity player)
        {
            super(player);
        }
    }
}

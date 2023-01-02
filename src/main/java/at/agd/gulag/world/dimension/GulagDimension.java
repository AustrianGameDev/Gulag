package at.agd.gulag.world.dimension;

import at.agd.gulag.Gulag;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class GulagDimension
{
    public static RegistryKey<World> GulagDim = RegistryKey.getOrCreateKey(Registry.WORLD_KEY,
            new ResourceLocation(Gulag.MODID, "gulag_dim"));
}

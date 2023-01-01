package at.agd.gulag.world.biome;

import at.agd.gulag.Gulag;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class GulagBiomes
{
    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(ForgeRegistries.BIOMES, Gulag.MODID);

    public static final RegistryObject<Biome> GULAG_BIOME = BIOMES.register("gulag_biome",
            () -> makeGulagBiome(() -> GulagConfiguredSurfaceBuilder.GULAG_SURFACE, 0.0f, 0.0f));

    private static Biome makeGulagBiome(final Supplier<ConfiguredSurfaceBuilder<?>> surfaceBuilder,
                                        float depth, float scale)
    {
        MobSpawnInfo.Builder mobspawninfo$builder = new MobSpawnInfo.Builder();
        BiomeGenerationSettings.Builder biomegenerationsettings$builder =
                (new BiomeGenerationSettings.Builder()).withSurfaceBuilder(surfaceBuilder);

        return (new Biome.Builder()).precipitation(Biome.RainType.NONE).category(Biome.Category.NONE)
                .depth(depth).scale(scale).temperature(0.0f).downfall(0.0f)
                .setEffects((new BiomeAmbience.Builder()).setWaterColor(4159204)
                        .setWaterFogColor(329011).setFogColor(10518688).withSkyColor(0)
                        .setMoodSound(MoodSoundAmbience.DEFAULT_CAVE).build())
                .withMobSpawnSettings(mobspawninfo$builder.copy())
                .withGenerationSettings(biomegenerationsettings$builder.build()).build();
    }

    public static void register(IEventBus eventBus)
    {
        BIOMES.register(eventBus);
    }
}

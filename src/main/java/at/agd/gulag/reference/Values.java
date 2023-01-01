package at.agd.gulag.reference;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Values
{
    public static final class Positions
    {
        public static final BlockPos gulagFightPos = new BlockPos(0.5, 1, 0.5);
        public static final BlockPos gulagEnemyPos = new BlockPos(0.5, 1, 26.5);
        public static final BlockPos gulagVisitPos = new BlockPos(-1.5, 7.0, -11.5);
    }

    public static final class Rotations
    {
        public static final float gulagVisitYawn = 0.0f;
        public static final int gulagVisitPitch = 0;
    }

    public static final class Items
    {
        public static final String[] TYPES = {"wooden", "stone", "golden", "iron", "diamond", "netherite"};
        public static final String[] ARMOR_TYPES = {"leather", "chainmail", "iron", "golden", "diamond", "netherite"};
        public static final String[] ARMOR_ITEMS = {"_boots", "_leggings", "_chestplate", "_helmet"};
    }

    public static final class Enchantments
    {
        public static final List<Enchantment> MELEE_ENCHANTMENTS = new ArrayList<>(Arrays.asList(
                new Enchantment((short)2, "fire_aspect"),
                new Enchantment((short)2, "knockback"),
                new Enchantment((short)5, "sharpness"),
                new Enchantment((short)5, "smite"),
                new Enchantment((short)3, "sweeping")));

        public static final List<Enchantment> BOW_ENCHANTMENTS = new ArrayList<>(Arrays.asList(
                new Enchantment((short)1, "flame"),
                new Enchantment((short)1, "infinity"),
                new Enchantment((short)5, "power"),
                new Enchantment((short)2, "punch")));

        public static final List<Enchantment> ARMOR_ENCHANTMENTS = new ArrayList<>(Arrays.asList(
                new Enchantment((short)4, "blast_protection"),
                new Enchantment((short)4, "fire_protection"),
                new Enchantment((short)4, "projectile_protection"),
                new Enchantment((short)4, "protection"),
                new Enchantment((short)3, "thorns")));

        public static class Enchantment
        {
            private short maxLvl;
            private String id;

            public Enchantment(short maxLvl, String id)
            {
                this.maxLvl = maxLvl;
                this.id = id;
            }

            public short getMaxLvl()
            {
                return maxLvl;
            }

            public String getId()
            {
                return id;
            }
        }
    }
}

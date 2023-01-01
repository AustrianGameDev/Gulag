package at.agd.gulag.entity;

import at.agd.gulag.reference.Values;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeaponBuilder
{
    private static Random rand = new Random();
    private static String mcId = "minecraft:";

    public static class EasyBuilder implements IWeaponBuilder
    {
        @Override
        public CompoundNBT getWeapon(CompoundNBT weapon, List<Values.Enchantments.Enchantment> enchantmentList)
        {
            CompoundNBT tag = new CompoundNBT();
            ListNBT enchantments = new ListNBT();

            // 10% chance of enchantment
            if(rand.nextInt(10) == 0)
            {
                CompoundNBT enchantmentNBT = new CompoundNBT();
                // Only one enchantment
                Values.Enchantments.Enchantment enchantment = enchantmentList.get(rand.nextInt(enchantmentList.size()));

                enchantmentNBT.putShort("lvl", (short)1);
                enchantmentNBT.putString("id", mcId + enchantment.getId());
                enchantments.add(enchantmentNBT);
            }

            tag.putInt("RepairCost", 1);
            tag.putInt("Damage", 0);
            tag.put("Enchantments", enchantments);
            weapon.put("tag", tag);

            return weapon;
        }
    }

    public static class NormalBuilder implements IWeaponBuilder
    {
        @Override
        public CompoundNBT getWeapon(CompoundNBT weapon, List<Values.Enchantments.Enchantment> enchantmentList)
        {
            CompoundNBT tag = new CompoundNBT();
            ListNBT enchantments = new ListNBT();

            // 50% chance of enchantment
            if(rand.nextBoolean())
            {
                // 1 - 2 enchantments
                int nrOfEnchantments = rand.nextInt(2) + 1;
                List<String> usedEnchantments = new ArrayList<>();

                for(int i = 0; i < nrOfEnchantments; i++)
                {
                    CompoundNBT enchantmentNBT = new CompoundNBT();
                    Values.Enchantments.Enchantment enchantment;

                    do
                    {
                        enchantment = enchantmentList.get(rand.nextInt(enchantmentList.size()));
                    }
                    while(usedEnchantments.contains(enchantment.getId()));
                    usedEnchantments.add(enchantment.getId());

                    enchantmentNBT.putShort("lvl", enchantment.getMaxLvl());
                    enchantmentNBT.putString("id", mcId + enchantment.getId());
                    enchantments.add(enchantmentNBT);
                }
            }

            tag.putInt("RepairCost", 1);
            tag.putInt("Damage", 0);
            tag.put("Enchantments", enchantments);
            weapon.put("tag", tag);

            return weapon;
        }
    }

    public static class HardBuilder implements IWeaponBuilder
    {
        @Override
        public CompoundNBT getWeapon(CompoundNBT weapon, List<Values.Enchantments.Enchantment> enchantmentList)
        {
            CompoundNBT tag = new CompoundNBT();
            ListNBT enchantments = new ListNBT();

            // 100% chance of enchantment
            // 2 - 4 enchantments
            int nrOfEnchantments = rand.nextInt(4 - 2 + 1) + 2;
            List<String> usedEnchantments = new ArrayList<>();

            for(int i = 0; i < nrOfEnchantments; i++)
            {
                CompoundNBT enchantmentNBT = new CompoundNBT();
                Values.Enchantments.Enchantment enchantment;

                do
                {
                    enchantment = enchantmentList.get(rand.nextInt(enchantmentList.size()));
                }
                while(usedEnchantments.contains(enchantment.getId()));
                usedEnchantments.add(enchantment.getId());

                enchantmentNBT.putShort("lvl", (short)(enchantment.getMaxLvl() * 2));
                enchantmentNBT.putString("id", mcId + enchantment.getId());
                enchantments.add(enchantmentNBT);
            }

            tag.putInt("RepairCost", 1);
            tag.putInt("Damage", 0);
            tag.put("Enchantments", enchantments);
            weapon.put("tag", tag);

            return weapon;
        }
    }

    public static CompoundNBT getBaseMeleeWeapon(int setType)
    {
        CompoundNBT meleeWeapon = new CompoundNBT();

        int typesLength = Values.Items.TYPES.length;
        String type = setType != -1 ? Values.Items.TYPES[setType] : Values.Items.TYPES[rand.nextInt(typesLength)];

        String id = mcId + type;

        switch (rand.nextInt(4))
        {
            case 0: // Sword
                id += "_sword";
                break;
            case 1: // Pickaxe
                id += "_pickaxe";
                break;
            case 2: // Axe
                id += "_axe";
                break;
            case 3: // Shovel
                id += "_shovel";
                break;
        }

        meleeWeapon.putString("id", id);
        meleeWeapon.putBoolean("Count", true);
        return meleeWeapon;
    }

    public static CompoundNBT getBaseBow()
    {
        CompoundNBT bow = new CompoundNBT();
        bow.putString("id", "minecraft:bow");
        bow.putBoolean("Count", true);
        return bow;
    }
}

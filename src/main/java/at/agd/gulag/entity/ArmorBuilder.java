package at.agd.gulag.entity;

import at.agd.gulag.reference.Values;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArmorBuilder
{
    private static Random rand = new Random();

    private static String[] armorItemsArray = Values.Items.ARMOR_ITEMS;
    private static String[] armorTypes = Values.Items.ARMOR_TYPES;
    private static List<Values.Enchantments.Enchantment> enchantmentList = Values.Enchantments.ARMOR_ENCHANTMENTS;
    private static String mcId = "minecraft:";

    public static class EasyBuilder implements IArmorBuilder
    {
        @Override
        public ListNBT getArmor(boolean isEnemy)
        {
            int armorChance, armorCount, enchantmentChance;

            // 50% chance of armor, regardless if enemy or player
            // true:  Enemy  -> 2 armor items, 30% chance for enchantment
            // false: Player -> 1 armor items, 10% chance for enchantment
            armorChance = 50;
            armorCount = isEnemy ? 2 : 1;
            enchantmentChance = isEnemy ? 30 : 10;

            if(!gotChance(armorChance))
            {
                return pseudoEmptyList();
            }

            return enchantArmor(getPopulatedArmorList(armorCount), enchantmentChance, 1, 1, EnchantmentModifier.ONE);
        }
    }

    public static class NormalBuilder implements IArmorBuilder
    {
        @Override
        public ListNBT getArmor(boolean isEnemy)
        {
            int armorChance, armorCount, enchantmentChance;

            // true:  Enemy  -> 60% chance for armor, 3 armor items, 70% chance for enchantment
            // false: Player -> 40% chance for armor, 1 - 2 armor items, 20% chance for enchantment
            armorChance = isEnemy ? 60 : 40;
            armorCount = isEnemy ? 3 : rand.nextInt(2) + 1;
            enchantmentChance = isEnemy ? 70 : 20;

            if(!gotChance(armorChance))
            {
                return pseudoEmptyList();
            }

            return enchantArmor(getPopulatedArmorList(armorCount), enchantmentChance, 2, 3, EnchantmentModifier.MAX);
        }
    }

    public static class HardBuilder implements IArmorBuilder
    {
        @Override
        public ListNBT getArmor(boolean isEnemy)
        {
            int armorChance, armorCount, enchantmentChance;

            // true:  Enemy  -> 80% chance for armor, 4 armor items, 90% chance for enchantment
            // false: Player -> 30% chance for armor, 2 - 3 armor items, 30% chance for enchantment
            armorChance = isEnemy ? 80 : 30;
            armorCount = isEnemy ? 4 : rand.nextInt(3 - 2 + 1) + 2;
            enchantmentChance = isEnemy ? 90 : 30;

            if(!gotChance(armorChance))
            {
                return pseudoEmptyList();
            }

            return enchantArmor(getPopulatedArmorList(armorCount), enchantmentChance, 2, 4, EnchantmentModifier.DOUBLE);
        }
    }

    private static ListNBT pseudoEmptyList()
    {
        ListNBT list = new ListNBT(); // list = []
        for(int i = 0; i < 4; i++)
        {
            list.add(new CompoundNBT());
        }
        return list; // list = [{}, {}, {}, {}]
    }

    private static String getRandomArmorType()
    {
        return armorTypes[rand.nextInt(armorTypes.length)];
    }

    private static ListNBT getPopulatedArmorList(int armorCount)
    {
        ListNBT armorItems = pseudoEmptyList();
        CompoundNBT armorItem;
        List<Integer> usedItemsIndices = new ArrayList<>();
        String armorType, id;
        int index;

        for(int i = 0; i < armorCount; i++)
        {
            do
            {
                index = rand.nextInt(armorItemsArray.length);
            }
            while(usedItemsIndices.contains(index));
            usedItemsIndices.add(index);

            armorType = getRandomArmorType();

            id = mcId + armorType + armorItemsArray[index];

            armorItem = new CompoundNBT();
            armorItem.putString("id", id);
            armorItem.putBoolean("Count", true);

            armorItems.set(index, armorItem);
        }

        return armorItems;
    }

    private static ListNBT enchantArmor(ListNBT armorSet, int chance, int minNrOfEnchantments,
                                        int maxNrOfEnchantments, EnchantmentModifier modifier)
    {
        // Loop for every armor piece
        for(int i = 0; i < 4; i++)
        {
            if(!gotChance(chance))
            {
                continue;
            }

            ListNBT enchantments = new ListNBT();
            List<String> usedEnchantments = new ArrayList<>();
            int nrOfEnchantments = rand.nextInt(maxNrOfEnchantments - minNrOfEnchantments + 1) + minNrOfEnchantments;

            for(int j = 0; j < nrOfEnchantments; j++)
            {
                Values.Enchantments.Enchantment enchantment;

                do
                {
                    enchantment = enchantmentList.get(rand.nextInt(enchantmentList.size()));
                }
                while(usedEnchantments.contains(enchantment.getId()));
                usedEnchantments.add(enchantment.getId());

                short lvl = (short) 0;

                switch(modifier)
                {
                    case ONE:
                        lvl = (short) 1;
                        break;
                    case MAX:
                        lvl = enchantment.getMaxLvl();
                        break;
                    case DOUBLE:
                        lvl = (short)(2 * enchantment.getMaxLvl());
                        break;
                }

                CompoundNBT enchantmentNBT = new CompoundNBT();
                enchantmentNBT.putShort("lvl", lvl);
                enchantmentNBT.putString("id", enchantment.getId());

                enchantments.add(enchantmentNBT);
            }

            CompoundNBT tag = new CompoundNBT();
            tag.putInt("Damage", 0);
            tag.put("Enchantments", enchantments);

            armorSet.getCompound(i).put("tag", tag);
        }
        return armorSet;
    }

    // chance -> In %
    private static boolean gotChance(int chance)
    {
        if(rand.nextInt(100) >= chance)
        {
            return false;
        }
        return true;
    }

    private enum EnchantmentModifier
    {
        ONE,
        MAX,
        DOUBLE
    }
}

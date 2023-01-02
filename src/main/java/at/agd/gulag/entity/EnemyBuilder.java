package at.agd.gulag.entity;

import at.agd.gulag.GulagController;
import at.agd.gulag.reference.Values;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import java.util.List;

public class EnemyBuilder
{
    private static String mcId = "minecraft:";
    private static List<Values.Enchantments.Enchantment> meleeE = Values.Enchantments.MELEE_ENCHANTMENTS;
    private static List<Values.Enchantments.Enchantment> bowE = Values.Enchantments.BOW_ENCHANTMENTS;

    public static Entity buildEnemy(Entity entity)
    {
        GulagController gulagController = GulagController.getInstance();
        IWeaponBuilder enemyStrategy = new WeaponBuilder.NormalBuilder();
        IArmorBuilder armorStrategy = new ArmorBuilder.NormalBuilder();

        switch(gulagController.getDifficulty())
        {
            case PEACEFUL:
            case EASY:
                enemyStrategy = new WeaponBuilder.EasyBuilder();
                armorStrategy = new ArmorBuilder.EasyBuilder();
                break;
            case NORMAL:
                enemyStrategy = new WeaponBuilder.NormalBuilder();
                armorStrategy = new ArmorBuilder.NormalBuilder();
                break;
            case HARD:
                enemyStrategy = new WeaponBuilder.HardBuilder();
                armorStrategy = new ArmorBuilder.HardBuilder();
                break;
        }

        CompoundNBT handItemsNbt = new CompoundNBT();
        ListNBT handItems = new ListNBT();
        ListNBT armorItems = armorStrategy.getArmor(true);

        // ToDo: Do all enemies

        if(entity instanceof ZombieEntity)
        {
            ZombieEntity zombieEntity = (ZombieEntity) entity;

            ((ZombieEntity) entity).writeAdditional(handItemsNbt);

            handItems.add(enemyStrategy.getWeapon(WeaponBuilder.getBaseMeleeWeapon(-1), meleeE));
            handItems.add(new CompoundNBT());

            handItemsNbt.put("HandItems", handItems);

            CompoundNBT zombieNBT = zombieEntity.serializeNBT();
            zombieNBT.remove("HandItems");
            zombieNBT.remove("ArmorItems");
            zombieNBT.put("HandItems", handItems);
            zombieNBT.put("ArmorItems", armorItems);
            zombieEntity.deserializeNBT(zombieNBT);

            entity = zombieEntity;
        }
        else if(entity instanceof SkeletonEntity)
        {
            SkeletonEntity skeletonEntity = (SkeletonEntity) entity;

            ((SkeletonEntity) entity).writeAdditional(handItemsNbt);

            handItems.add(enemyStrategy.getWeapon(WeaponBuilder.getBaseBow(), bowE));
            handItems.add(new CompoundNBT());

            handItemsNbt.put("HandItems", handItems);

            CompoundNBT skeletonNBT = skeletonEntity.serializeNBT();
            skeletonNBT.remove("HandItems");
            skeletonNBT.remove("ArmorItems");
            skeletonNBT.put("HandItems", handItems);
            skeletonNBT.put("ArmorItems", armorItems);
            skeletonEntity.deserializeNBT(skeletonNBT);

            entity = skeletonEntity;
        }
        else if(entity instanceof StrayEntity)
        {
            StrayEntity strayEntity = (StrayEntity) entity;

            ((StrayEntity) entity).writeAdditional(handItemsNbt);

            handItems.add(enemyStrategy.getWeapon(WeaponBuilder.getBaseBow(), bowE));
            handItems.add(new CompoundNBT());

            handItemsNbt.put("HandItems", handItems);

            CompoundNBT strayNBT = strayEntity.serializeNBT();
            strayNBT.remove("HandItems");
            strayNBT.remove("ArmorItems");
            strayNBT.put("HandItems", handItems);
            strayNBT.put("ArmorItems", armorItems);
            strayEntity.deserializeNBT(strayNBT);
        }

        return entity;
    }
}

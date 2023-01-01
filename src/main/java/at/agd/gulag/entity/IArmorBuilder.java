package at.agd.gulag.entity;

import net.minecraft.nbt.ListNBT;

public interface IArmorBuilder
{
    public ListNBT getArmor(boolean isEnemy);
}

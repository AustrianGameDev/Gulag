package at.agd.gulag.entity;

import at.agd.gulag.reference.Values;
import net.minecraft.nbt.CompoundNBT;

import java.util.List;

public interface IWeaponBuilder
{
    public CompoundNBT getWeapon(CompoundNBT weapon, List<Values.Enchantments.Enchantment> enchantmentList);
}

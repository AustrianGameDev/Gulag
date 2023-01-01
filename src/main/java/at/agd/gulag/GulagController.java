package at.agd.gulag;

import at.agd.gulag.db.PlayerPosInfoDB;
import at.agd.gulag.entity.ArmorBuilder;
import at.agd.gulag.entity.IArmorBuilder;
import at.agd.gulag.events.GulagEventHooks;
import at.agd.gulag.world.dimension.GulagDimension;
import at.agd.gulag.world.dimension.GulagTeleportHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class GulagController
{
    private static GulagController instance;

    private static Random rand = new Random();
    private static PlayerPosInfoDB pidb;

    private static UUID currentPlayerUUID;
    private static UUID currentEnemyUUID;
    private static UUID removeEnemyUUID;
    private static int enemyUuidCount;
    private static List<UUID> waitingPlayers;
    private static Difficulty difficulty;
    private static boolean isHardcore;

    private static MinecraftServer server;

    private GulagController()
    {
        pidb = PlayerPosInfoDB.getInstance();
        currentPlayerUUID = null;
        removeEnemyUUID = null;
        enemyUuidCount = 1;
        waitingPlayers = new ArrayList<>();
        server = null;
    }

    public static GulagController getInstance()
    {
        if(instance == null)
        {
            instance = new GulagController();
        }
        return instance;
    }

    public void setDifficulty(Difficulty newDifficulty)
    {
        difficulty = newDifficulty;
    }

    public void setDifficulty(Difficulty newDifficulty, boolean newIsHardcore)
    {
        difficulty = newDifficulty;
        isHardcore = newIsHardcore;
    }

    public Difficulty getDifficulty()
    {
        return difficulty;
    }

    public boolean isHardcore()
    {
        return isHardcore;
    }

    public static void onPlayerIntoGulag(PlayerEntity player)
    {
        if(server == null)
        {
            server = player.getServer();
        }

        boolean playerExistsInDb = pidb.exists(player.getEntityId());

        // True: There is already a player fighting in the Gulag -> Teleport new player to a waiting area
        if(currentPlayerUUID != null && playerExistsInDb)
        {
            waitingPlayers.add(player.getUniqueID());
            GulagTeleportHelper.toWaitingArea(player);
            return;
        }

        if(playerExistsInDb)
        {
            GulagTeleportHelper.onPlayerRespawn(player);

            playerSetup(player);

            currentPlayerUUID = player.getUniqueID();
            currentEnemyUUID = GulagSpawner.spawnEntity(player, enemyUuidCount);
            enemyUuidCount++;
            removeEnemyUUID = null;
        }
    }

    public void checkIfEnemyDied(LivingDeathEvent event)
    {
        Entity entity = event.getEntity();
        World entityWorld = entity.getEntityWorld();

        // Check if an entity died in the Gulag -> return if not
        if(!entityWorld.isRemote)
        {
            MinecraftServer server = entityWorld.getServer();

            if(server != null)
            {
                if(entityWorld != server.getWorld(GulagDimension.GulagDim))
                {
                    return;
                }
            }
        }
        else
        {
            return;
        }

        if(entity.getUniqueID() == currentEnemyUUID)
        {
            PlayerEntity player = event.getEntity().getServer().getPlayerList().getPlayerByUUID(currentPlayerUUID);
            player.inventory.clear();
            player.setGameType(GameType.SURVIVAL); // ToDo: Set to previous game type
            GulagEventHooks.fireGulagWinEvent(player);
            currentPlayerUUID = null;

            checkIfPlayerIsWaiting(event);
        }
    }

    public static void playerSetup(PlayerEntity player)
    {
        // Set player to Adventure Mode
        player.setGameType(GameType.ADVENTURE);
        player.inventory.clear();

        // Give player weapon and armor
        IArmorBuilder armorBuilder = new ArmorBuilder.NormalBuilder();
        Item item = Items.STICK;

        switch(difficulty)
        {
            case PEACEFUL:
            case EASY:
                item = Items.WOODEN_SWORD;
                armorBuilder = new ArmorBuilder.EasyBuilder();
                break;
            case NORMAL:
                item = Items.IRON_SWORD;
                armorBuilder = new ArmorBuilder.NormalBuilder();
                break;
            case HARD:
                item = Items.DIAMOND_SWORD;
                armorBuilder = new ArmorBuilder.HardBuilder();
                break;
        }

        CompoundNBT playerNBT = player.serializeNBT();
        ListNBT armorSet = armorBuilder.getArmor(false);

        for(int i = 0; i < armorSet.size(); i++)
        {
            CompoundNBT armorItem = armorSet.getCompound(i);
            byte slot = Byte.parseByte(Integer.toString(i + 100));

            armorItem.putByte("Slot", slot);
            armorSet.set(i, armorItem);
        }
        playerNBT.remove("Inventory");
        playerNBT.put("Inventory", armorSet);
        player.deserializeNBT(playerNBT);

        player.setHeldItem(Hand.MAIN_HAND, new ItemStack(item));
        // ToDo: Maybe give player enchanted weapon -> WeaponBuilder
        // ToDo: Maybe give player a shield or totem of undying (on hard difficulty)
    }

    public void playerDiedInGulag(LivingDeathEvent event)
    {
        removeEnemyUUID = currentEnemyUUID;
        currentPlayerUUID = null;
        currentEnemyUUID = null;
        checkForEnemyToRemove();
        checkIfPlayerIsWaiting(event);
    }

    public void checkForEnemyToRemove()
    {
        if(removeEnemyUUID == null)
        {
            return;
        }

        ServerWorld world = server.getWorld(GulagDimension.GulagDim);
        Entity entity = world.getEntityByUuid(removeEnemyUUID);

        if(entity != null)
        {
            world.removeEntity(entity, false);
            removeEnemyUUID = null;
        }
    }

    private void checkIfPlayerIsWaiting(LivingDeathEvent event)
    {
        // True: No players are waiting
        if(waitingPlayers.size() == 0)
        {
            return;
        }

        UUID playerUUID = waitingPlayers.remove(0);

        PlayerEntity player = event.getEntity().getServer().getPlayerList().getPlayerByUUID(playerUUID);
        onPlayerIntoGulag(player);
    }
}

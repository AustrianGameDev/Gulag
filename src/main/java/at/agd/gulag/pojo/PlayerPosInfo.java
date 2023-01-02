package at.agd.gulag.pojo;

import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class PlayerPosInfo
{
    private int id;
    private UUID uuid;
    private BlockPos position;

    public PlayerPosInfo(int id, UUID uuid, BlockPos position)
    {
        this.id = id;
        this.uuid = uuid;
        this.position = position;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }

    public BlockPos getPosition()
    {
        return position;
    }

    public void setPosition(BlockPos position)
    {
        this.position = position;
    }

    @Override
    public String toString()
    {
        return id + ", " + uuid + ", " + position.toString();
    }
}

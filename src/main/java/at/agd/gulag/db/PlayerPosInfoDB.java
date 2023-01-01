package at.agd.gulag.db;

import at.agd.gulag.pojo.PlayerPosInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerPosInfoDB
{
    private static PlayerPosInfoDB instance;

    private List<PlayerPosInfo> playerPosInfos;

    private PlayerPosInfoDB()
    {
        playerPosInfos = new ArrayList<>();
    }

    public static PlayerPosInfoDB getInstance()
    {
        if(instance == null)
        {
            instance = new PlayerPosInfoDB();
        }
        return instance;
    }

    public PlayerPosInfo getPlayerInfoById(int id)
    {
        return playerPosInfos.stream().filter(pi -> pi.getId() == id).findFirst().orElse(null);
    }

    public boolean addPlayerPosInfo(PlayerPosInfo ppi)
    {
        if(getPlayerInfoById(ppi.getId()) == null)
        {
            playerPosInfos.add(ppi);
            return true;
        }
        return false;
    }

    public void removePlayerPosInfoById(int id)
    {
        playerPosInfos.removeIf(ppi -> ppi.getId() == id);
    }

    public boolean exists(int id)
    {
        // true: Player is in the list because he died but not in the Gulag
        // false: Player ist not in the list because he died in the Gulag

        Optional<PlayerPosInfo> ppi = playerPosInfos.stream().filter(p -> p.getId() == id).findFirst();
        return ppi.isPresent();
    }

    public void printList()
    {
        if(playerPosInfos.size() == 0)
        {
            System.out.println("No players in the list");
            return;
        }

        System.out.println("****************************");
        for(PlayerPosInfo ppi : playerPosInfos)
        {
            System.out.println(ppi.toString());
        }
        System.out.println("****************************");
    }
}

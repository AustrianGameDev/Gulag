package at.agd.gulag.util;

import java.io.InputStream;

public class Util
{
    private static Util instance;

    private Util()
    {
    }

    private static Util getInstance()
    {
        if(instance == null)
        {
            instance = new Util();
        }
        return instance;
    }

    public static InputStream getInputStream(String file)
    {
        InputStream is = getInstance().getClass().getClassLoader().getResourceAsStream(file);
        return is;
    }
}

package mulitplayer.Server;

import com.esotericsoftware.kryo.Kryo;

/**
 * Created by productionaccount on 1/5/16.
 */
public class KryoRegistery {
    public static void registerPacketData(Kryo kryo){
        kryo.register(String.class);
        kryo.register(int.class);
        kryo.register(double.class);
        kryo.register(float.class);
        kryo.register(byte.class);
    }
}

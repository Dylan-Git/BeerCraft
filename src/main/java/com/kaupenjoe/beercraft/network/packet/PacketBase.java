package com.kaupenjoe.beercraft.network.packet;

import com.kaupenjoe.beercraft.network.NetworkHandler;

public abstract class PacketBase implements IPacket {

    protected final int id;
    protected final NetworkHandler handler;

    protected PacketBase(int id, NetworkHandler handler) {

        this.id = id;
        this.handler = handler;
    }

    @Override
    public byte getId() {

        return (byte) id;
    }

    @Override
    public NetworkHandler getHandler()
    {
        return handler;
    }

}

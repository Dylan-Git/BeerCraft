package com.kaupenjoe.beercraft.network.packet;

import com.kaupenjoe.beercraft.BeerCraft;
import com.kaupenjoe.beercraft.network.NetworkHandler;
import com.kaupenjoe.beercraft.tileentity.FluidTankTest;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;

public class TileGuiPacket extends PacketBase implements IPacketClient {

    protected BlockPos pos;
    protected PacketBuffer buffer;

    public TileGuiPacket()
    {
        super(1, BeerCraft.NETWORK_HANDLER);
    }

    @Override
    public void handleClient() {

        World world = BeerCraft.proxy.getClientWorld();
        if (world == null) {
            LogManager.getLogger().error("Client world is null! (Is this being called on the server?)");
            return;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof FluidTankTest) {
            ((FluidTankTest)tile).handleGuiPacket(buffer);
        }
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeBlockPos(pos);
        buf.writeBytes(buffer);
    }

    @Override
    public void read(PacketBuffer buf) {

        buffer = buf;
        pos = buffer.readBlockPos();
    }

    public static void sendToClientFluid(FluidTankTest tile, ServerPlayerEntity player) {

        if (tile.getWorld().isRemote()) {
            return;
        }

        TileGuiPacket packet = new TileGuiPacket();
        packet.pos = tile.getPos();
        packet.buffer = tile.getGuiPacket(new PacketBuffer(Unpooled.buffer()));
        packet.sendToPlayer(player);
    }

}
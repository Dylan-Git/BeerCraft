package com.kaupenjoe.beercraft.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.ICustomPacket;
import org.spongepowered.asm.mixin.Shadow;

public class FluidPacket implements ICustomPacket
{
    private PacketBuffer data;

}

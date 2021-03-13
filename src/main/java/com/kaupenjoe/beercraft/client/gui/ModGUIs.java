package com.kaupenjoe.beercraft.client.gui;

import com.kaupenjoe.beercraft.util.Registration;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;

public class ModGUIs
{
    public static final RegistryObject<ContainerType<TankContainer>> TANK_CONTAINER
            = Registration.CONTAINERS.register("tank_container",
            () -> IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getEntityWorld();
                return new TankContainer(windowId, world, pos, inv, inv.player);
            }));



    public static void register() { }
}

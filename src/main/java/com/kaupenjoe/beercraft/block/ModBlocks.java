package com.kaupenjoe.beercraft.block;

import com.kaupenjoe.beercraft.util.BeerTabs;
import com.kaupenjoe.beercraft.util.Registration;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks
{
    public static RegistryObject<Block> TANK_TEST = register("tank_test",
            () -> new TankTestBlock(AbstractBlock.Properties.from(Blocks.STONE)));

    public static void register()
    {

    }

    private static <T extends Block>RegistryObject<T> register(String name, Supplier<T> block)
    {
        RegistryObject<T> toReturn = Registration.BLOCKS.register(name, block);
        Registration.ITEMS.register(name, () -> new BlockItem(toReturn.get(),
                new Item.Properties().group(BeerTabs.BEERTAB)));
        return toReturn;
    }
}

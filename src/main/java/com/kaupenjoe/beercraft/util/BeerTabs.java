package com.kaupenjoe.beercraft.util;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BeerTabs
{
    public static final ItemGroup BEERTAB = new ItemGroup("beertab")
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(Items.DIAMOND);
        }
    };
}

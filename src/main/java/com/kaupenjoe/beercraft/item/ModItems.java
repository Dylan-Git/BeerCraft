package com.kaupenjoe.beercraft.item;

import com.kaupenjoe.beercraft.block.ModFluids;
import com.kaupenjoe.beercraft.util.BeerTabs;
import com.kaupenjoe.beercraft.util.Registration;
import net.minecraft.item.*;
import net.minecraftforge.fml.RegistryObject;

public class ModItems
{
    public static final RegistryObject<Item> OIL_BUCKET =
            Registration.ITEMS.register("oil_bucket",
                    () -> new BucketItem(ModFluids.OIL_FLUID::get,
                            new Item.Properties().group(BeerTabs.BEERTAB).maxStackSize(1)));



    public static void register() { }
}

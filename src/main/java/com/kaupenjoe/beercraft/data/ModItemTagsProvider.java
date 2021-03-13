package com.kaupenjoe.beercraft.data;

import com.kaupenjoe.beercraft.BeerCraft;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModItemTagsProvider extends ItemTagsProvider
{
    public ModItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(dataGenerator, blockTagProvider, BeerCraft.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerTags()
    {
        super.registerTags();
    }
}

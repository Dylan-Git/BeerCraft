package com.kaupenjoe.beercraft.data;

import com.kaupenjoe.beercraft.BeerCraft;
import com.kaupenjoe.beercraft.block.ModBlocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModBlockTagsProvider extends BlockTagsProvider
{
    public ModBlockTagsProvider(DataGenerator generatorIn, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(generatorIn, BeerCraft.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerTags()
    {


        super.registerTags();
    }
}

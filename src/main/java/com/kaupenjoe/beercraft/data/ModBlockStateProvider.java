package com.kaupenjoe.beercraft.data;

import com.kaupenjoe.beercraft.BeerCraft;
import com.kaupenjoe.beercraft.block.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider
{
    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper)
    {
        super(gen, BeerCraft.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels()
    {

    }
}

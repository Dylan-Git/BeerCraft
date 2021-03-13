package com.kaupenjoe.beercraft.data;

import com.kaupenjoe.beercraft.BeerCraft;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider
{
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator, BeerCraft.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels()
    {

    }

    private ItemModelBuilder builder(ModelFile itemGenerated, String name)
    {
        return getBuilder(name).parent(itemGenerated).texture("layer0", "item/"  + name);
    }
}

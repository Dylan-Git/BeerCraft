package com.kaupenjoe.beercraft.data;

import com.kaupenjoe.beercraft.BeerCraft;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = BeerCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators
{
    private static final String[] LOCALE_CODES = new String[] {
            "en_us"
    };

    private DataGenerators() { }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        addLanguageProviders(generator);

        generator.addProvider(new ModBlockStateProvider(generator, existingFileHelper));
        generator.addProvider(new ModItemModelProvider(generator, existingFileHelper));

        ModBlockTagsProvider block_tags = new ModBlockTagsProvider(generator, existingFileHelper);

        generator.addProvider(block_tags);
        generator.addProvider(new ModItemTagsProvider(generator, block_tags, existingFileHelper));
        generator.addProvider(new ModRecipeProvider(generator));
        generator.addProvider(new ModLootTableProvider(generator));
        generator.addProvider(new ModAdvancementProvider(generator));
    }

    private static void addLanguageProviders(DataGenerator generator){
        for(String locale : LOCALE_CODES)
        {
            generator.addProvider(new ModLanguageProvider(generator, locale));
        }
    }
}

package com.kaupenjoe.beercraft.data;

import com.kaupenjoe.beercraft.BeerCraft;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider
{
    public ModLanguageProvider(DataGenerator gen, String locale)
    {
        super(gen, BeerCraft.MOD_ID, locale);
    }

    @Override
    protected void addTranslations()
    {
        String locale = this.getName().replace("Languages: ", "");

        switch (locale)
        {
            case "en_us":
                add("item.mccourse.copper_ingot", "Copper Ingot");

                break;
            default:
                break;
        }
    }
}

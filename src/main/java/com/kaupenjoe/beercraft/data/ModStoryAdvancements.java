package com.kaupenjoe.beercraft.data;

import com.kaupenjoe.beercraft.BeerCraft;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Consumer;

public class ModStoryAdvancements implements Consumer<Consumer<ModAdvancement>>
{
    @Override
    public void accept(Consumer<ModAdvancement> consumer)
    {
        ModAdvancement.Builder.builder()
                .advancement(Advancement.Builder.builder()
                    .withParentId(new ResourceLocation("story/iron_tools"))
                    .withDisplay(Items.APPLE,
                            new TranslationTextComponent("advancements.story.copper_block.title"),
                            new TranslationTextComponent("advancements.story.copper_block.description"),
                        null, FrameType.TASK, true, true, false)
                        .withCriterion("copper_block", InventoryChangeTrigger.Instance.forItems(Items.APPLE)))
                        .build(consumer, new ResourceLocation(BeerCraft.MOD_ID, "story/copper_block"));
    }
}

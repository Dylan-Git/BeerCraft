package com.kaupenjoe.beercraft.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ModAdvancementProvider implements IDataProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final DataGenerator generator;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();


    private final List<Consumer<Consumer<ModAdvancement>>> advancements =
            ImmutableList.of(new ModStoryAdvancements());


    public ModAdvancementProvider(DataGenerator generator)
    {
        this.generator = generator;
    }

    @Override
    public void act(DirectoryCache cache) throws IOException
    {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<ModAdvancement> consumer = (advancement) -> {
            if (!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            } else {
                Path path1 = getPath(path, advancement);

                try {
                    IDataProvider.save(GSON, cache, advancement.serialize(), path1);
                } catch (IOException ioexception) {
                    LOGGER.error("Couldn't save advancement {}", path1, ioexception);
                }

            }
        };

        for(Consumer<Consumer<ModAdvancement>> consumer1 : this.advancements) {
            consumer1.accept(consumer);
        }
    }

    private static Path getPath(Path pathIn, ModAdvancement advancement)
    {
        return pathIn.resolve("data/" + advancement.getId().getNamespace()
            + "/advancements/" + advancement.getId().getPath() + ".json");
    }

    @Override
    public String getName()
    {
        return "Advancements";
    }
}

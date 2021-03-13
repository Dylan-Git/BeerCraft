package com.kaupenjoe.beercraft.data;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class ModAdvancement
{
    private final ResourceLocation id;
    private final Advancement.Builder builder;

    private ModAdvancement(ResourceLocation idIn, Advancement.Builder builderIn) {
        this.id = idIn;
        this.builder = builderIn;
    }

    public JsonObject serialize() {
        return this.builder.serialize();
    }

    public ResourceLocation getId() {
        return id;
    }

    public static class Builder {

        private Advancement.Builder builder;

        private Builder() {}

        public static Builder builder() {
            return new Builder();
        }

        public Builder advancement(Advancement.Builder builderIn) {
            this.builder = builderIn;
            return this;
        }

        public ModAdvancement build(Consumer<ModAdvancement> consumer, ResourceLocation id) {
            ModAdvancement advancement = new ModAdvancement(id, builder);
            consumer.accept(advancement);
            return advancement;
        }
    }
}

package com.theendercore.trowel.data.gen

import com.theendercore.trowel.Trowel
import com.theendercore.trowel.Trowel.MODID
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementEntry
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.item.Items
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class TrowelData : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(gen: FabricDataGenerator) {
        val pack = gen.createPack()
        pack.addProvider(::RecipeProvider)
        pack.addProvider(::AdvancementProvider)
    }
}

class RecipeProvider(output: FabricDataOutput, registries: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricRecipeProvider(output, registries) {
    override fun getRecipeGenerator(
        registries: RegistryWrapper.WrapperLookup,
        exporter: RecipeExporter,
    ): RecipeGenerator = object : RecipeGenerator(registries, exporter) {
        override fun generate() {
            createShaped(RecipeCategory.TOOLS, Trowel.TROWEL)
                .pattern(" II")
                .pattern("S  ")
                .input('I', Items.IRON_INGOT)
                .input('S', Items.STICK)
                .criterion("has_iron_ingot", conditionsFromItem(Items.IRON_INGOT))
                .criterion("has_stick", conditionsFromItem(Items.STICK))
                .criterion("has_trowel", conditionsFromItem(Trowel.TROWEL))
                .offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(MODID, MODID)))
        }
    }

    override fun getName(): String = "Trowel Recipes"
}

class AdvancementProvider(output: FabricDataOutput, registries: CompletableFuture<RegistryWrapper.WrapperLookup>) :
    FabricAdvancementProvider(output, registries) {
    override fun generateAdvancement(
        registries: RegistryWrapper.WrapperLookup,
        exporter: Consumer<AdvancementEntry>,
    ) {
        val parent = AdvancementEntry(Identifier.ofVanilla("story/smelt_iron"), null)
        Advancement.Builder.create()
            .parent(parent)
            .display(
                Trowel.TROWEL,
                Text.translatable("advancements.story.trowel.title"),
                Text.translatable("advancements.story.trowel.description"),
                null,
                AdvancementFrame.TASK,
                true,
                true,
                false
            )
            .criterion(MODID, InventoryChangedCriterion.Conditions.items(Trowel.TROWEL))
            .build(exporter, "minecraft:story/$MODID")
    }
}

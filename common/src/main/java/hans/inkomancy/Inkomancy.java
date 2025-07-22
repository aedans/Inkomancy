package hans.inkomancy;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import dev.architectury.event.events.common.LootEvent;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import hans.inkomancy.inks.BlackInk;
import hans.inkomancy.inks.RedInk;
import hans.inkomancy.morphemes.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Inkomancy {
  public static final String MOD_ID = "inkomancy";

  public static final Supplier<RegistrarManager> MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));
  public static final Registrar<Item> ITEMS = MANAGER.get().get(Registries.ITEM);
  public static final Registrar<Block> BLOCKS = MANAGER.get().get(Registries.BLOCK);
  public static final Registrar<EntityType<?>> ENTITY_TYPE = MANAGER.get().get(Registries.ENTITY_TYPE);
  public static final Registrar<DataComponentType<?>> DATA_COMPONENT_TYPE = MANAGER.get().get(Registries.DATA_COMPONENT_TYPE);
  public static final Registrar<RecipeSerializer<?>> RECIPE_SERIALIZER = MANAGER.get().get(Registries.RECIPE_SERIALIZER);
  public static final Registrar<RecipeType<?>> RECIPE_TYPE = MANAGER.get().get(Registries.RECIPE_TYPE);

  public static BlockBehaviour.Properties blockSettings(ResourceKey<Block> key) {
    return BlockBehaviour.Properties.of().setId(key);
  }

  public static Item.Properties itemSettings(ResourceKey<Item> key) {
    return new Item.Properties().useItemDescriptionPrefix().setId(key);
  }

  public static RegistrySupplier<Item> registerItem(String name, Function<ResourceKey<Item>, Item> item) {
    var rl = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    var key = ResourceKey.create(Registries.ITEM, rl);
    return ITEMS.register(rl, () -> item.apply(key));
  }

  public static RegistrySupplier<InkBlock> registerInkBlock(Ink ink) {
    var rl = ResourceLocation.fromNamespaceAndPath(MOD_ID, ink.name + "_ink");
    var key = ResourceKey.create(Registries.BLOCK, rl);
    var sound = new SoundType(1, 1,
        SoundEvents.INK_SAC_USE, SoundEvents.EMPTY, SoundEvents.INK_SAC_USE, SoundEvents.EMPTY, SoundEvents.EMPTY);
    return BLOCKS.register(rl,
        () -> new InkBlock(blockSettings(key)
            .noCollission()
            .noOcclusion()
            .instabreak()
            .pushReaction(PushReaction.DESTROY)
            .sound(sound)));
  }

  public static RegistrySupplier<InkItem> registerInkItem(Ink ink) {
    var rl = ResourceLocation.fromNamespaceAndPath(MOD_ID, ink.name + "_ink");
    var key = ResourceKey.create(Registries.ITEM, rl);
    return ITEMS.register(rl, () -> new InkItem(ink.getBlock(), itemSettings(key)));
  }

  public static RegistrySupplier<MorphemeItem> registerMorphemeItem(Morpheme morpheme) {
    var rl = ResourceLocation.fromNamespaceAndPath(MOD_ID, morpheme.name + "_morpheme");
    var key = ResourceKey.create(Registries.ITEM, rl);
    return ITEMS.register(rl, () -> new MorphemeItem(morpheme, itemSettings(key)));
  }

  public static RegistrySupplier<EntityType<InkBallEntity>> registerInkBallEntity() {
    var rl = ResourceLocation.fromNamespaceAndPath(MOD_ID, "ink_ball");
    var key = ResourceKey.create(Registries.ENTITY_TYPE, rl);
    return ENTITY_TYPE.register(
        rl,
        () -> EntityType.Builder.<InkBallEntity>of(InkBallEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build(key)
    );
  }

  public static final RegistrySupplier<Item> SPELL_SCRIBE = registerItem("spell_scribe", key -> new SpellScribeItem(itemSettings(key), BlackInk.INSTANCE));
  public static final RegistrySupplier<Item> MIRROR = registerItem("mirror", key -> new MagicItem(itemSettings(key), RedInk.INSTANCE));
  public static final RegistrySupplier<Item> BLUE_QUILL = registerItem("blue_quill", key -> new MagicItem(itemSettings(key), RedInk.INSTANCE));
  public static final RegistrySupplier<Item> RED_QUILL = registerItem("red_quill", key -> new MagicItem(itemSettings(key), RedInk.INSTANCE));
  public static final RegistrySupplier<Item> INK_WAND = registerItem("ink_wand", key -> new MagicItem(itemSettings(key), RedInk.INSTANCE));
  public static final RegistrySupplier<Item> FLOWER_WAND = registerItem("flower_wand", key -> new MagicItem(itemSettings(key), RedInk.INSTANCE));

  public static final RegistrySupplier<Item> INK_HELPER = registerItem("ink_helper", key -> new InkHelperItem(itemSettings(key)));
  public static final RegistrySupplier<Item> INK_BALL = registerItem("ink_ball", key -> new Item(itemSettings(key)));
  public static final RegistrySupplier<EntityType<InkBallEntity>> INK_BALL_ENTITY = registerInkBallEntity();

  public static final RegistrySupplier<DataComponentType<Boolean>> CONJURED_COMPONENT_TYPE = DATA_COMPONENT_TYPE.register(
      ResourceLocation.fromNamespaceAndPath(MOD_ID, "conjured"),
      () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build());

  public static final RegistrySupplier<DataComponentType<Spell>> SPELL_COMPONENT_TYPE = DATA_COMPONENT_TYPE.register(
      ResourceLocation.fromNamespaceAndPath(MOD_ID, "spell"),
      () -> DataComponentType.<Spell>builder().persistent(Spell.CODEC).networkSynchronized(Spell.PACKET_CODEC).build());

  public static final ResourceLocation INKOMANCY_TAB_RL = ResourceLocation.fromNamespaceAndPath(MOD_ID, "inkomancy");
  public static final ResourceKey<CreativeModeTab> INKOMANCY_TAB_RK = ResourceKey.create(Registries.CREATIVE_MODE_TAB, INKOMANCY_TAB_RL);

  public static List<Item> items() {
    var items = new ArrayList<Item>();
    for (var ink : Ink.getInks()) {
      items.add(ink.getItem());
    }

    items.add(SPELL_SCRIBE.get());
    items.add(MIRROR.get());
    items.add(BLUE_QUILL.get());
    items.add(RED_QUILL.get());
    items.add(INK_WAND.get());
    items.add(FLOWER_WAND.get());

    items.add(INK_HELPER.get());
    items.add(INK_BALL.get());

    for (var morpheme : Morpheme.getMorphemes()) {
      items.add(morpheme.getItem());
    }

    return items;
  }

  public static void init() {
    for (var ink : Ink.getInks()) {
      ink.register();
    }

    for (var morpheme : Morpheme.getMorphemes()) {
      morpheme.register();
    }

    RECIPE_SERIALIZER.register(TransmutationRecipe.Type.ID, () -> TransmutationRecipe.Serializer.INSTANCE);
    RECIPE_TYPE.register(TransmutationRecipe.Type.ID, () -> TransmutationRecipe.Type.INSTANCE);

    var tables = new HashMap<ResourceKey<LootTable>, Integer>();

    tables.put(BuiltInLootTables.ABANDONED_MINESHAFT, 8);
    tables.put(BuiltInLootTables.ANCIENT_CITY, 8);
    tables.put(BuiltInLootTables.BASTION_BRIDGE, 4);
    tables.put(BuiltInLootTables.BASTION_HOGLIN_STABLE, 4);
    tables.put(BuiltInLootTables.BASTION_OTHER, 4);
    tables.put(BuiltInLootTables.BASTION_TREASURE, 1);
    tables.put(BuiltInLootTables.BURIED_TREASURE, 1);
    tables.put(BuiltInLootTables.DESERT_PYRAMID, 4);
    tables.put(BuiltInLootTables.END_CITY_TREASURE, 1);
    tables.put(BuiltInLootTables.JUNGLE_TEMPLE, 2);
    tables.put(BuiltInLootTables.NETHER_BRIDGE, 8);
    tables.put(BuiltInLootTables.RUINED_PORTAL, 2);
    tables.put(BuiltInLootTables.SHIPWRECK_TREASURE, 2);
    tables.put(BuiltInLootTables.SIMPLE_DUNGEON, 2);
    tables.put(BuiltInLootTables.STRONGHOLD_CORRIDOR, 8);
    tables.put(BuiltInLootTables.STRONGHOLD_CROSSING, 2);
    tables.put(BuiltInLootTables.WOODLAND_MANSION, 8);

    LootEvent.MODIFY_LOOT_TABLE.register((ResourceKey<LootTable> key, LootEvent.LootTableModificationContext ctx, boolean b) -> {
      if (tables.containsKey(key)) {
        var spellScribe = LootItem.lootTableItem(SPELL_SCRIBE.get()).setWeight(2);

        var mirrorSpell = new Spell(SourceMorpheme.INSTANCE, new Spell(SwapMorpheme.INSTANCE));
        var mirror = LootItem.lootTableItem(MIRROR.get()).apply(SetComponentsFunction.setComponent(SPELL_COMPONENT_TYPE.get(), mirrorSpell));

        var quillSpell = new Spell(SourceMorpheme.INSTANCE, new Spell(TransmuteMorpheme.INSTANCE, new Spell(VoidMorpheme.INSTANCE)));
        var redQuill = LootItem.lootTableItem(RED_QUILL.get()).apply(SetComponentsFunction.setComponent(SPELL_COMPONENT_TYPE.get(), quillSpell));

        var inkWandSpell = new Spell(SourceMorpheme.INSTANCE, new Spell(StarMorpheme.INSTANCE));
        var inkWand = LootItem.lootTableItem(INK_WAND.get()).apply(SetComponentsFunction.setComponent(SPELL_COMPONENT_TYPE.get(), inkWandSpell));

        var flowerWandSpell = new Spell(SourceMorpheme.INSTANCE, new Spell(GrowMorpheme.INSTANCE, new Spell(VoidMorpheme.INSTANCE)));
        var flowerWand = LootItem.lootTableItem(FLOWER_WAND.get()).apply(SetComponentsFunction.setComponent(SPELL_COMPONENT_TYPE.get(), flowerWandSpell));

        var items = List.of(spellScribe, mirror, redQuill, inkWand, flowerWand);
        var poolBuilder = LootPool.lootPool().add(EmptyLootItem.emptyItem().setWeight((items.size() + 1) * (tables.get(key) - 1)));

        for (var item : items) {
          poolBuilder.add(item);
        }

        ctx.addPool(poolBuilder);
      }
    });
  }
}

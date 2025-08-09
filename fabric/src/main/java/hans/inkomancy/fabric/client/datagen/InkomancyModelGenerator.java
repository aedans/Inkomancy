package hans.inkomancy.fabric.client.datagen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import hans.inkomancy.Ink;
import hans.inkomancy.InkBlock;
import hans.inkomancy.Inkomancy;
import hans.inkomancy.Morpheme;
import hans.inkomancy.inks.ArdentInk;
import hans.inkomancy.inks.ConductiveInk;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static net.minecraft.core.Direction.*;
import static net.minecraft.client.data.models.blockstates.VariantProperties.Rotation.*;

public class InkomancyModelGenerator extends FabricModelProvider {
  public InkomancyModelGenerator(FabricDataOutput generator) {
    super(generator);
  }

  public static final Map<VariantProperties.Rotation, VariantProperties.Rotation> NEXT_ROTATION = Maps.newEnumMap(
      ImmutableMap.of(R0, R90, R90, R180, R180, R270, R270, R0));

  public void generateInkBlockModel(BlockModelGenerators generator, String name, String color) {
    for (VariantProperties.Rotation rotation : VariantProperties.Rotation.values()) {
      int rotationValue = rotation.ordinal() * 90;

      JsonObject root = new JsonObject();
      root.addProperty("ambientocclusion", false);

      JsonObject textures = new JsonObject();
      textures.addProperty("texture", Inkomancy.MOD_ID + ":block/" + color + "_" + name);
      textures.addProperty("particle", Inkomancy.MOD_ID + ":block/" + color + "_" + name);

      JsonArray elements = getJsonElements(rotationValue);

      root.add("textures", textures);
      root.add("elements", elements);
      root.addProperty("render_type", "minecraft:translucent");

      var identifier = ResourceLocation.fromNamespaceAndPath(Inkomancy.MOD_ID, "block/" + color + "_" + name + "_" + rotationValue);
      generator.modelOutput.accept(identifier, () -> root);
    }
  }

  private static @NotNull JsonArray getJsonElements(int rotationValue) {
    var upUV = new JsonArray();
    upUV.add(0);
    upUV.add(0);
    upUV.add(16);
    upUV.add(16);

    JsonObject up = new JsonObject();
    up.addProperty("texture", "#texture");
    up.addProperty("rotation", rotationValue);
    up.add("uv", upUV);

    JsonObject faces = new JsonObject();
    faces.add("up", up);

    JsonArray to = new JsonArray();
    to.add(16);
    to.add(0.25);
    to.add(16);

    JsonArray from = new JsonArray();
    from.add(0);
    from.add(0.25);
    from.add(0);

    JsonObject element = new JsonObject();
    element.addProperty("shade", false);
    element.add("faces", faces);
    element.add("to", to);
    element.add("from", from);

    JsonArray elements = new JsonArray();
    elements.add(element);
    return elements;
  }

  private void updateDirectionalSupplier(
      String model,
      String color,
      VariantProperties.Rotation rotation,
      Map<String, Pair<Condition, List<Variant>>> variantMap,
      Predicate<Direction> predicate) {
    for (Direction facing : Direction.values()) {
      var when = Condition.condition();
      var whenString = new StringBuilder();

      when.term(BlockStateProperties.FACING, facing);
      whenString.append(facing);
      for (Direction d : Direction.Plane.HORIZONTAL) {
        var requirement = predicate.test(d);
        when.term(InkBlock.DIRECTION_CONNECTION.get(d), requirement);
        whenString.append(requirement ? "1" : "0");
      }

      var x = R0;
      var y = R0;

      if (facing == DOWN) {
        x = R180;
        y = R180;
      } else if (facing == NORTH) {
        x = R270;
        y = R180;
      } else if (facing == EAST) {
        x = R270;
        y = R270;
      } else if (facing == SOUTH) {
        x = R270;
      } else if (facing == WEST) {
        x = R270;
        y = R90;
      }

      var identifier = ResourceLocation.fromNamespaceAndPath(Inkomancy.MOD_ID, "block/" + color + "_" + model + "_" + (rotation.ordinal() * 90));
      var variants = variantMap.getOrDefault(whenString.toString(), new Pair<>(when, new ArrayList<>()));
      variants.getSecond().add(Variant.variant()
          .with(VariantProperties.MODEL, identifier)
          .with(VariantProperties.X_ROT, x)
          .with(VariantProperties.Y_ROT, y));
      variantMap.put(whenString.toString(), variants);
    }
  }

  private void updateAllDirectionalSupplier(
      MultiPartGenerator supplier,
      String model,
      String color,
      BiPredicate<Direction, Direction> predicate) {
    var rotation = R0;
    var variantMap = new HashMap<String, Pair<Condition, List<Variant>>>();

    for (Direction direction : Direction.Plane.HORIZONTAL) {
      updateDirectionalSupplier(model, color, rotation, variantMap, d -> predicate.test(direction, d));
      rotation = NEXT_ROTATION.get(rotation);
    }

    for (var pair : variantMap.values()) {
      supplier.with(pair.getFirst(), pair.getSecond());
    }
  }

  public void generateInkBlockStateModel(BlockModelGenerators generator, String name, String color, Block block) {
    var s = MultiPartGenerator.multiPart(block);

    updateAllDirectionalSupplier(s, name + "_dot", color, (d1, d2) -> false);
    updateAllDirectionalSupplier(s, name + "_four", color, (d1, d2) -> true);
    updateAllDirectionalSupplier(s, name + "_end", color, (d1, d2) -> d1 == d2);
    updateAllDirectionalSupplier(s, name + "_straight", color, (d1, d2) -> d1 == d2 || d1 == d2.getOpposite());
    updateAllDirectionalSupplier(s, name + "_corner", color, (d1, d2) -> d1 == d2 || d1.getClockWise() == d2);
    updateAllDirectionalSupplier(s, name + "_three", color,
        (d1, d2) -> d1 == d2 || d1 == d2.getOpposite() || d1.getClockWise() == d2);

    generator.blockStateOutput.accept(s);

    generateInkBlockModel(generator, name + "_corner", color);
    generateInkBlockModel(generator, name + "_dot", color);
    generateInkBlockModel(generator, name + "_end", color);
    generateInkBlockModel(generator, name + "_four", color);
    generateInkBlockModel(generator, name + "_straight", color);
    generateInkBlockModel(generator, name + "_three", color);
  }

  @Override
  public void generateBlockStateModels(BlockModelGenerators generator) {
    for (var color : Inkomancy.COLORS) {
      generateInkBlockStateModel(generator, "ardent_ink", color, ArdentInk.INSTANCE.getBlock(color));
      generateInkBlockStateModel(generator, "conductive_ink", color, ConductiveInk.INSTANCE.getBlock(color));
//      generateInkBlockStateModel(generator, "void_ink_", VoidInk.INSTANCE.getBlock());
    }

    generator.woodProvider(Inkomancy.FOG_LOG.get()).logWithHorizontal(Inkomancy.FOG_LOG.get()).wood(Inkomancy.FOG_WOOD.get());
    generator.woodProvider(Inkomancy.STRIPPED_FOG_LOG.get()).logWithHorizontal(Inkomancy.STRIPPED_FOG_LOG.get()).wood(Inkomancy.STRIPPED_FOG_WOOD.get());
    generator.createTrivialCube(Inkomancy.FOG_PLANKS.get());
    generator.createTrivialCube(Inkomancy.FOG_PLANKS_ERODED.get());
    generator.createTrivialCube(Inkomancy.FOG_LEAVES.get());
  }

  @Override
  public void generateItemModels(ItemModelGenerators generator) {
    for (var ink : Ink.getInks()) {
      for (var color : Inkomancy.COLORS) {
        generator.generateFlatItem(ink.getItem(color), ModelTemplates.FLAT_ITEM);
      }
    }

    for (var morpheme : Morpheme.getMorphemes()) {
      generator.generateFlatItem(morpheme.getItem(), ModelTemplates.FLAT_ITEM);
    }

    generator.generateFlatItem(Inkomancy.INKY.get(), ModelTemplates.FLAT_ITEM);
    generator.generateFlatItem(Inkomancy.AMANUENSIS.get(), ModelTemplates.FLAT_ITEM);
    generator.generateFlatItem(Inkomancy.MIRROR.get(), ModelTemplates.FLAT_ITEM);
    generator.generateFlatItem(Inkomancy.BLUE_QUILL.get(), ModelTemplates.FLAT_ITEM);
    generator.generateFlatItem(Inkomancy.RED_QUILL.get(), ModelTemplates.FLAT_ITEM);
    generator.generateFlatItem(Inkomancy.INK_WAND.get(), ModelTemplates.FLAT_ITEM);
    generator.generateFlatItem(Inkomancy.FLOWER_WAND.get(), ModelTemplates.FLAT_ITEM);
    generator.generateFlatItem(Inkomancy.MAGMA_PICKAXE.get(), ModelTemplates.FLAT_ITEM);
    generator.generateFlatItem(Inkomancy.VOID_SHOVEL.get(), ModelTemplates.FLAT_ITEM);
    generator.generateFlatItem(Inkomancy.HAMMER.get(), ModelTemplates.FLAT_ITEM);

    generator.generateFlatItem(Inkomancy.INK_BALL.get(), ModelTemplates.FLAT_ITEM);
  }
}
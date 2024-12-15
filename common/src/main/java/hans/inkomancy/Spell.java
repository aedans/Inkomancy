package hans.inkomancy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public record Spell(Morpheme morpheme,
                    List<Spell> connected,
                    @Nullable BlockPos pos,
                    @Nullable Direction dir) implements TooltipProvider {
  public static final Codec<Spell> CODEC = Codec.recursive("spell",
      (Codec<Spell> CODEC) -> RecordCodecBuilder.create(instance -> instance.group(
              Morpheme.CODEC.fieldOf("morpheme").forGetter(Spell::morpheme),
              CODEC.listOf().fieldOf("connected").forGetter(Spell::connected))
          .apply(instance, Spell::new)));

  public static final StreamCodec<ByteBuf, Spell> PACKET_CODEC = StreamCodec.recursive(
      (StreamCodec<ByteBuf, Spell> PACKET_CODEC) -> StreamCodec.composite(
          Morpheme.PACKET_CODEC, Spell::morpheme,
          ByteBufCodecs.collection(ArrayList<Spell>::new, PACKET_CODEC), Spell::connected,
          Spell::new));

  public Spell(Morpheme morpheme, List<Spell> connected) {
    this(morpheme, connected, null, null);
  }

  public Spell(Morpheme morpheme, Spell... spell) {
    this(morpheme, List.of(spell));
  }

  public Spell base() {
    return new Spell(this.morpheme, this.connected.stream().map(Spell::base).toList());
  }

  @Override
  public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip, TooltipFlag type) {
    tooltip.accept(Component.literal("Inscribed spell").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF55FF))));
  }

  @Override
  public String toString() {
    if (connected.isEmpty()) {
      return morpheme.toString();
    } else {
      return morpheme.toString() + connected;
    }
  }
}

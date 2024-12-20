package hans.inkomancy;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import org.jetbrains.annotations.NotNull;

public class InkomancyClient {
  public static void init() {
    EntityRendererRegistry.register(Inkomancy.INK_BALL_ENTITY, ThrownItemRenderer::new);
  }

  public static class TransparentDelegateVertexConsumer implements VertexConsumer {
    private final VertexConsumer delegate;

    public TransparentDelegateVertexConsumer(VertexConsumer delegate) {
      this.delegate = delegate;
    }

    @Override
    public @NotNull VertexConsumer addVertex(float x, float y, float z) {
      return delegate.addVertex(x, y, z);
    }

    @Override
    public @NotNull VertexConsumer setColor(int red, int green, int blue, int alpha) {
      return delegate.setColor(red, green, blue, (alpha * 3) / 4);
    }

    @Override
    public @NotNull VertexConsumer setUv(float u, float v) {
      return delegate.setUv(u, v);
    }

    @Override
    public @NotNull VertexConsumer setUv1(int u, int v) {
      return delegate.setUv1(u, v);
    }

    @Override
    public @NotNull VertexConsumer setUv2(int u, int v) {
      return delegate.setUv2(u, v);
    }

    @Override
    public @NotNull VertexConsumer setNormal(float x, float y, float z) {
      return delegate.setNormal(x, y, z);
    }
  }
}

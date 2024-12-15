package hans.inkomancy.neoforge;

import hans.inkomancy.InkomancyClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = "inkomancy", dist = Dist.CLIENT)
public class InkomancyNeoForgeClient {
  public InkomancyNeoForgeClient() {
    InkomancyClient.init();
  }
}

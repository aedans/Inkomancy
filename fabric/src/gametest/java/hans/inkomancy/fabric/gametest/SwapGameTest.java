package hans.inkomancy.fabric.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

import static hans.inkomancy.fabric.gametest.Spells.*;

/**
 * Full coverage of {@code swap} behavior. Each test is a spell a player could paint, paired with what
 * casting it should do. `swap` reads each child as a landing spot; children carrying an entity are the
 * things that move, and each moves to a spot that isn't its own.
 */
public class SwapGameTest implements FabricGameTest {

  // swap[hole, hole]: an entity in each hole -> the two trade places.
  @GameTest(template = EMPTY_STRUCTURE)
  public void swapExchangesTwoEntities(GameTestHelper helper) {
    helper.spawn(EntityType.PIG, 1, 2, 1);
    helper.spawn(EntityType.COW, 3, 2, 3);

    cast(helper, swap(hole(1, 2, 1), hole(3, 2, 3)));

    helper.assertEntityPresent(EntityType.PIG, 3, 2, 3);
    helper.assertEntityPresent(EntityType.COW, 1, 2, 1);
    helper.succeed();
  }

  // swap[read[hole], hole]: `read` masks off ENTITIES, so read[hole] is a destination *position* and
  // the bare hole supplies the moving entity -> that entity teleports onto the read position.
  @GameTest(template = EMPTY_STRUCTURE)
  public void readTeleportsEntityOntoTarget(GameTestHelper helper) {
    helper.spawn(EntityType.PIG, 1, 2, 1);
    helper.spawn(EntityType.COW, 5, 2, 5);

    cast(helper, swap(read(hole(5, 2, 5)), hole(1, 2, 1)));

    helper.assertEntityPresent(EntityType.PIG, 5, 2, 5); // moved onto the cow
    helper.assertEntityPresent(EntityType.COW, 5, 2, 5); // unmoved
    helper.succeed();
  }

  // A bare `hole` with no entity in it resolves as a POSITION (ENTITIES came back empty), so it acts as
  // a destination just like read[hole] -> the entity from the occupied hole teleports there.
  @GameTest(template = EMPTY_STRUCTURE)
  public void emptyHoleActsAsDestination(GameTestHelper helper) {
    helper.spawn(EntityType.PIG, 1, 2, 1);

    cast(helper, swap(hole(4, 2, 4), hole(1, 2, 1))); // hole(4,2,4) is empty -> destination

    helper.assertEntityPresent(EntityType.PIG, 4, 2, 4);
    helper.succeed();
  }

  // One source, several destinations: the entity lands on one of them (random) but never on itself.
  @GameTest(template = EMPTY_STRUCTURE)
  public void entityPicksAmongMultipleDestinations(GameTestHelper helper) {
    var pig = helper.spawn(EntityType.PIG, 1, 2, 1);

    cast(helper, swap(hole(1, 2, 1), hole(4, 2, 4), hole(7, 2, 7))); // two empty holes -> destinations

    helper.assertTrue(isAt(helper, pig, 4, 2, 4) || isAt(helper, pig, 7, 2, 7),
        "entity should land on one of the empty-hole destinations");
    helper.assertFalse(isAt(helper, pig, 1, 2, 1), "entity should not stay on its own spot");
    helper.succeed();
  }

  // swap[hole] with a single entity and no destination and no caster: sent home to the world spawn.
  @GameTest(template = EMPTY_STRUCTURE)
  public void loneEntitySentToWorldSpawnWithoutCaster(GameTestHelper helper) {
    var pig = helper.spawn(EntityType.PIG, 1, 2, 1);

    cast(helper, swap(hole(1, 2, 1)));

    var spawn = helper.getLevel().getSharedSpawnPos();
    assertAt(pig, spawn.getBottomCenter().add(0, .5, 0));
    helper.succeed();
  }

  // Same "send home" fallback, but the caster has a respawn point -> that wins over the world spawn.
  @GameTest(template = EMPTY_STRUCTURE)
  public void loneEntitySentToCasterRespawnPoint(GameTestHelper helper) {
    var caster = helper.makeMockServerPlayerInLevel();
    var respawn = helper.absolutePos(new BlockPos(5, 2, 5));
    caster.setRespawnPosition(helper.getLevel().dimension(), respawn, 0f, true, false);
    var pig = helper.spawn(EntityType.PIG, 1, 2, 1);

    cast(helper, swap(hole(1, 2, 1)), caster);

    helper.assertEntityPresent(EntityType.PIG, 5, 2, 5);
    helper.succeed();
  }

  // Two entities stacked on the same spot with nowhere else to go: nobody moves (no crash).
  @GameTest(template = EMPTY_STRUCTURE)
  public void stackedEntitiesStayPut(GameTestHelper helper) {
    var pig = helper.spawn(EntityType.PIG, 1, 2, 1);
    var cow = helper.spawn(EntityType.COW, 1, 2, 1);
    var here = helper.absolutePos(new BlockPos(1, 2, 1)).getBottomCenter();
    pig.moveTo(here.x, here.y, here.z); // force identical positions -> each other's only spot is "self"
    cow.moveTo(here.x, here.y, here.z);

    cast(helper, swap(hole(1, 2, 1))); // one hole yields both entities as sources

    helper.assertEntityPresent(EntityType.PIG, 1, 2, 1);
    helper.assertEntityPresent(EntityType.COW, 1, 2, 1);
    helper.succeed();
  }

  // `hole` reads any Entity, so dropped items are sources too: swap[hole, hole] trades two item stacks.
  @GameTest(template = EMPTY_STRUCTURE)
  public void swapExchangesTwoItemEntities(GameTestHelper helper) {
    helper.spawnItem(Items.DIRT, 1, 2, 1);
    helper.spawnItem(Items.STONE, 3, 2, 3);

    cast(helper, swap(hole(1, 2, 1), hole(3, 2, 3)));

    helper.assertItemEntityPresent(Items.DIRT, new BlockPos(3, 2, 3), 1.0);
    helper.assertItemEntityPresent(Items.STONE, new BlockPos(1, 2, 1), 1.0);
    helper.succeed();
  }

  // Item entities and mobs are interchangeable as swap sources: a mob and a dropped item trade places.
  @GameTest(template = EMPTY_STRUCTURE)
  public void swapExchangesItemAndMob(GameTestHelper helper) {
    helper.spawn(EntityType.PIG, 1, 2, 1);
    helper.spawnItem(Items.DIAMOND, 3, 2, 3);

    cast(helper, swap(hole(1, 2, 1), hole(3, 2, 3)));

    helper.assertEntityPresent(EntityType.PIG, 3, 2, 3);
    helper.assertItemEntityPresent(Items.DIAMOND, new BlockPos(1, 2, 1), 1.0);
    helper.succeed();
  }

  // Bare `self` is the caster, so swap[self] is a lone source with nowhere to swap -> sent home.
  @GameTest(template = EMPTY_STRUCTURE)
  public void swapSelfSendsCasterHome(GameTestHelper helper) {
    var caster = helper.makeMockServerPlayerInLevel();
    caster.setRespawnPosition(helper.getLevel().dimension(), helper.absolutePos(new BlockPos(6, 2, 6)), 0f, true, false);
    var start = helper.absolutePos(new BlockPos(1, 2, 1)).getBottomCenter();
    caster.moveTo(start.x, start.y, start.z);

    cast(helper, swap(self()), caster);

    helper.assertEntityPresent(EntityType.PLAYER, 6, 2, 6); // recalled to the respawn point
    helper.succeed();
  }

  // swap[self, hole]: the caster is one source and an entity is the other -> they trade places.
  @GameTest(template = EMPTY_STRUCTURE)
  public void swapSelfWithEntity(GameTestHelper helper) {
    var caster = helper.makeMockServerPlayerInLevel();
    var start = helper.absolutePos(new BlockPos(1, 2, 1)).getBottomCenter();
    caster.moveTo(start.x, start.y, start.z);
    helper.spawn(EntityType.PIG, 3, 2, 3);

    cast(helper, swap(self(), hole(3, 2, 3)), caster);

    helper.assertEntityPresent(EntityType.PLAYER, 3, 2, 3);
    helper.assertEntityPresent(EntityType.PIG, 1, 2, 1);
    helper.succeed();
  }
}

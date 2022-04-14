package GameEngine.Components.TerrianComponents;


import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Terrain;

import java.util.Arrays;


public class MapBuilderTerrainGenerator extends TerrainGenerator{
   // Attributes
   private int[] world;

   // Constructor
   public MapBuilderTerrainGenerator(GameObject parent, int seed) {
      super(parent, seed);
   }


   // Methods
   @Override
   public int[] createWorld() {
      // Init world
      world = new int[width * height];
      Arrays.fill(world, Terrain.WALL);

      return world;
   }

   @Override
   public int[] getWorld() {
      return world;
   }
}

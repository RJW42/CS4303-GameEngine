package GameEngine.Components.TerrianComponents;


import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Terrain;

import java.util.Arrays;


public class LoadedTerrainGenerator extends TerrainGenerator{
   // Attributes
   public int[] world;

   // Constructor
   public LoadedTerrainGenerator(GameObject parent, int seed) {
      super(parent, seed);
   }


   // Methods
   public void loadTerrain(String file_loc){
      // Load the world into this terrain generator
      TerrainLoader.loadTerrain(this, file_loc);
   }

   @Override
   public int[] createWorld() {
      if(world == null){
         throw new IllegalArgumentException("loadTerrain must be called prior to create world");
      }
      return world;
   }

   @Override
   public int[] getWorld() {
      return world;
   }
}

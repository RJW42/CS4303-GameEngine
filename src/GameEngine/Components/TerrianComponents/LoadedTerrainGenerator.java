package GameEngine.Components.TerrianComponents;


import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Core.Terrain;


public class LoadedTerrainGenerator extends TerrainGenerator{
   // Attributes
   public int[] world;
   public int[] special_tiles;

   // Constructor
   public LoadedTerrainGenerator(GameObject parent, int seed) {
      super(parent, seed);
   }


   // Methods
   public void loadTerrain(String file_loc){
      // Load the world into this terrain generator
      TerrainLoader.loadTerrain(this, file_loc);

      Terrain.WIDTH = getWidth();
      Terrain.HEIGHT = getHeight();
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

   @Override
   public int[] getSpecialTiles() {
      return special_tiles;
   }
}

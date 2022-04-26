package GameEngine.Components.TerrianComponents;


import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Core.Terrain;

import java.util.Arrays;


public class MapEditorTerrainGenerator extends TerrainGenerator{
   // Attributes
   private int[] world;
   private int[] special_tiles;

   // Constructor
   public MapEditorTerrainGenerator(GameObject parent, int seed) {
      super(parent, seed);
   }


   // Methods
   @Override
   public int[] createWorld() {
      // Init world
      world = new int[width * height];
      special_tiles = new int[width * height];
      Arrays.fill(world, Terrain.AIR);
      Arrays.fill(special_tiles, Terrain.EMPTY);

      for(int i = 0; i < width; i++){
         world[getIndex(i, 0)] = Terrain.WALL;
         world[getIndex(i, height - 1)] = Terrain.WALL;
      }
      for(int i = 0; i < height; i++){
         world[getIndex(0, i)] = Terrain.WALL;
         world[getIndex(width - 1, i)] = Terrain.WALL;
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

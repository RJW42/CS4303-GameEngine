package GameEngine.Components.TerrianComponents;


import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Terrain;

import java.util.Arrays;


public class BasicTerrainGenerator extends TerrainGenerator{
   // Attributes
   private int[] world;

   // Constructor
   public BasicTerrainGenerator(GameObject parent, int seed) {
      super(parent, seed);
   }


   // Methods 
   @Override
   public int[] createWorld() {
      // Init world
      world = new int[width * height];
      Arrays.fill(world, Terrain.AIR);

      // Add walls to world
      for(int x = 0; x < width; x++) {
         world[getIndex(x, 0)] = Terrain.WALL;
         world[getIndex(x, height - 1)] = Terrain.WALL;
      }
      for(int y= 0; y < height; y++) {
         world[getIndex(0, y)] = Terrain.WALL;
         world[getIndex(width - 1, y)] = Terrain.WALL;
      }
      world[getIndex(1, 1)] = Terrain.WALL;
      world[getIndex(5, 5)] = Terrain.WALL;
      world[getIndex(4, 5)] = Terrain.WALL;
      world[getIndex(3, 5)] = Terrain.WALL;

      return world;
   }

   @Override
   public int[] getWorld() {
      return world;
   }
}
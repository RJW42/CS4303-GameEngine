package GameEngine.Components.TerrianComponents;

import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Core.Terrain;
import processing.core.PVector;

import java.util.ArrayList;

public abstract class TerrainGenerator extends Component {
   // Attributes
   protected int width, height, seed;

   public PVector player_spawn_loc;
   public ArrayList<PVector> monster_spawn_locs;

   // Constructor
   public TerrainGenerator(GameObject parent, int seed){
      super(parent);
      this.seed = seed;

      this.player_spawn_loc = new PVector(0,0);
      this.monster_spawn_locs = new ArrayList<>();
   }

   public int getWidth(){
      return width;
   }

   public int getHeight(){
      return height;
   }

   // Methods
   public abstract int[] createWorld();
   public abstract int[] getWorld();
   public abstract int[] getSpecialTiles();


   public void setConfig(int width, int height){
      this.height = height;
      this.width = width;
   }


   public int getIndex(int x, int y){
      return (y * width) + x;
   }

   public int getIndexFromWorldPos(float x, float y){
      x = x / Terrain.CELL_SIZE;
      y = y / Terrain.CELL_SIZE;
      return getIndex((int)x, (int)y);
   }

   public boolean validWalkCord(int x, int y){
      return x >= 0 && y >= 0 && x < width && y < height;
   }

   public PVector getPosFromIndex(int index) {
      return new PVector(index % width + (float)Terrain.CELL_SIZE/2f, index / width + (float)Terrain.CELL_SIZE/2f);
   }

   public interface TerrainSupplier {
      public TerrainGenerator get(GameObject parent, int seed);
   }
}

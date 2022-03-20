package GameEngine.Levels;

import GameEngine.Components.TerrianComponents.AdvancedTerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.GameObjects.Terrain;
import GameEngine.GameEngine;

import java.util.Random;


public class TestLevel extends Level{
   // Attributes
   boolean stop = false;

   // Constructor
   public TestLevel(GameEngine sys) {
      super(sys);
   }

   // Methods
   public void drawBackground(){
      sys.background(0);
   }

   public void start() {
      // Init world
      sys.initWorld();

      // Create terrian
      int seed = 357988076;
      seed = new Random().nextInt();
      System.out.println(seed);

      Terrain terrain = new Terrain(sys, seed, AdvancedTerrainGenerator::new);

      TerrainGenerator generator = terrain.getComponent(TerrainGenerator.class);
      generator.setConfig(Terrain.WIDTH, Terrain.HEIGHT); //, Terrain.WALK_ITERATIONS, Terrain.WALK_STEPS);
      generator.createWorld();

      //sys.spawn(new Ball(sys, new PVector(1, 5f)), 0);
      sys.terrain = terrain;
      sys.spawn(terrain, 0);
   }

   public boolean updateAndCanAdvance() {
      return false;
   }

   public Level advance() {
      return null;
   }
}

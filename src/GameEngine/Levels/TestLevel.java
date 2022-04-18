package GameEngine.Levels;

import GameEngine.Components.TerrianComponents.BasicTerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.GameObjects.Player;
import GameEngine.GameObjects.Terrain;
import GameEngine.GameEngine;
import processing.core.PVector;

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

      Terrain terrain = new Terrain(sys, seed, BasicTerrainGenerator::new);

      TerrainGenerator generator = terrain.getComponent(TerrainGenerator.class);
      generator.setConfig(Terrain.WIDTH, Terrain.HEIGHT); //, Terrain.WALK_ITERATIONS, Terrain.WALK_STEPS);
      generator.createWorld();

      sys.terrain = terrain;
      sys.spawn(terrain, 0);
      sys.spawn(new Player(sys, new PVector(3, 3)), 0);
//      sys.spawn(new GameOver(sys, 0), 0);
   }

   public boolean updateAndCanAdvance() {
      return false;
   }

   public Level advance() {
      return null;
   }
}

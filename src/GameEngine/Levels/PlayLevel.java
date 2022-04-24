package GameEngine.Levels;

import GameEngine.Components.TerrianComponents.LoadedTerrainGenerator;
import GameEngine.GameObjects.Player;
import GameEngine.GameObjects.Pointer;
import GameEngine.GameObjects.Terrain;
import GameEngine.GameEngine;

import java.util.Random;


public class PlayLevel extends Level{
   // Attributes
   private String file_name;

   // Constructor
   public PlayLevel(GameEngine sys, String file_name) {
      super(sys);

      // Init attributes
      this.file_name = file_name;
   }

   // Methods
   public void drawBackground(){
      sys.background(0);
   }

   public void start() {
      // Create terrian
      int seed = new Random().nextInt();
      System.out.println(seed);

      Terrain terrain = new Terrain(sys, seed, LoadedTerrainGenerator::new);

      LoadedTerrainGenerator generator = terrain.getComponent(LoadedTerrainGenerator.class);
      generator.loadTerrain(file_name);

      // Init world
      sys.initWorld(generator.getWidth(), generator.getHeight());

      // Spawn entities
      sys.terrain = terrain;
      sys.spawn(terrain, 0);

      Player player = new Player(sys, generator.player_spawn_loc.copy(), 0);
      sys.spawn(player, 2);
      sys.chase_object = player;
   }

   public boolean updateAndCanAdvance() {
      return false;
   }

   public Level advance() {
      return null;
   }
}

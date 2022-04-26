package GameEngine.Levels;

import GameEngine.Components.TerrianComponents.LoadedTerrainGenerator;
import GameEngine.GameObjects.Core.Director;
import GameEngine.GameObjects.Core.Monster;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.GameEngine;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PVector;

import java.util.Random;


public class PlayLevel extends Level{
   // Attributes
   private String file_name;
   private InputManager.Key restart;
   private InputManager.Key menu;
   private Level advance;

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
      // Get restart keys
      restart = sys.input_manager.getKey("RESTART_LEVEL");
      menu = sys.input_manager.getKey("EXIT_TO_MENU");

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
      sys.spawn(new Director(sys), 0);

      Player player = new Player(sys, generator.player_spawn_loc.copy());
      sys.spawn(player, 2);
      sys.chase_object = player;

      sys.spawn(new Monster(sys, new PVector(2, 2)), 2);
   }

   public boolean updateAndCanAdvance() {
      if(restart.pressed){
         advance = new PlayLevel(sys, file_name);
      } else if(menu.pressed) {
         advance = new MainMenu(sys);
      }

      return advance != null;
   }

   public Level advance() {
      return advance;
   }
}

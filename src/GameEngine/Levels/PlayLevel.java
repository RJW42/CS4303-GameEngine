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
   public static final int DESIRED_WALLS = 20;

   private String file_name;
   private InputManager.Key restart;
   private InputManager.Key menu;
   private Level advance;
   private LoadedTerrainGenerator generator;

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

      // Create objects
      init_terrain();
      init_managers();
      init_player();

      // Spawn monsters
      sys.spawn(new Monster(sys, new PVector(2, 2)), 2);
   }

   private void init_terrain(){
      int seed = new Random().nextInt();
      System.out.println(seed);

      sys.terrain = new Terrain(sys, seed, LoadedTerrainGenerator::new);
      generator = sys.terrain.getComponent(LoadedTerrainGenerator.class);
      generator.loadTerrain(file_name);

      // Init world
      sys.initWorld(generator.getWidth(), generator.getHeight());
      sys.spawn(sys.terrain, 0);
   }

   private void init_managers(){
      sys.spawn(new Director(sys), 0);
   }

   private void init_player(){
      // Create player
      Player player = new Player(sys, generator.player_spawn_loc.copy());
      sys.spawn(player, 2);

      // Set the player as the chase object
      sys.chase_object = player;

      float desired_x_zoom = GameEngine.SCREEN_WIDTH / (DESIRED_WALLS * GameEngine.PIXEL_TO_METER);
      float desired_y_zoom = GameEngine.SCREEN_HEIGHT / (DESIRED_WALLS * GameEngine.PIXEL_TO_METER);

      sys.chase_zoom = Math.min(desired_x_zoom, desired_y_zoom);
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

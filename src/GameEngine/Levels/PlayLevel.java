package GameEngine.Levels;

import GameEngine.Components.AIComponents.Timer;
import GameEngine.Components.ForceManager;
import GameEngine.Components.TerrianComponents.LoadedTerrainGenerator;
import GameEngine.GameObjects.Core.Director;
import GameEngine.GameObjects.Core.Monster;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.GameEngine;
import GameEngine.Utils.Managers.InputManager;

import java.util.Random;


public class PlayLevel extends Level{
   // Attributes
   public static final int DESIRED_WALLS = 20;

   private final String file_name;
   private InputManager.Key restart;
   private InputManager.Key menu;
   private Level advance;
   private LoadedTerrainGenerator generator;
   private Timer timer;

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
      init_objects();
      generator.spawn_monsters();
      generator.spawn_goal();
   }


   public void player_dead(){
      Player.ACTIVE = false;
      Timer.ACTIVE = false;
      Monster.ACTIVE = false;
      ForceManager.ACTIVE = false;
      System.out.println("dead");
   }


   public void player_reached_goal(){
      advance = new GameOver(sys, file_name, timer.game_time);
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


   private void init_objects(){
      // Create player
      Player player = new Player(sys, generator.player_spawn_loc.copy());
      sys.spawn(player, 2);

      // Create director
      Director director = new Director(sys, player);
      timer = director.getComponent(Timer.class);
      sys.spawn(director, 0);
   }
}

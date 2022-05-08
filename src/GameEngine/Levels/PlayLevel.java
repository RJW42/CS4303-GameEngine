package GameEngine.Levels;

import GameEngine.Components.AIComponents.Timer;
import GameEngine.Components.PlayerComponents.Powerups;
import GameEngine.Components.TerrianComponents.LoadedTerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainLoader;
import GameEngine.Components.TerrianComponents.TerrainRenderer;
import GameEngine.GameObjects.Core.Director;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.Core.Powerup;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.GameEngine;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PVector;

import java.util.Random;


public class PlayLevel extends Level{
   // Attributes
   public static final String[] BACKGROUND_MUSIC = new String[]{"play_1"};
   public static final String[] COMBAT_MUSIC = new String[]{"play_intense_1", "play_intense_2"};
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
      sys.audio_manager.start_background_music(BACKGROUND_MUSIC);
      sys.audio_manager.add_combat_music(COMBAT_MUSIC);
      generator.spawn_monsters();
      generator.spawn_goal();

      sys.spawn(new Powerup(sys, new PVector(4.5f, 1.5f), Powerups.GUN_BONUS), 2);
   }


   public void restart(){
      advance = new PlayLevel(sys, file_name);
   }

   public void menu(){
      advance = new MainMenu(sys);
   }


   public void player_reached_goal(){
      TerrainLoader.saveTerrain(generator, sys.terrain.getComponent(TerrainRenderer.class), file_name);

      advance = new GameOver(sys, file_name, timer.game_time);
   }


   public boolean updateAndCanAdvance() {
      sys.audio_manager.update_background_music();
      if(restart.pressed){
         restart();
      } else if(menu.pressed) {
         menu();
      }

      return advance != null;
   }

   public Level advance() {
      sys.audio_manager.cancel_background_music();
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

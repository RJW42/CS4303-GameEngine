package GameEngine.Levels;

import GameEngine.Components.AIComponents.Timer;
import GameEngine.Components.ForceManager;
import GameEngine.Components.TerrianComponents.LoadedTerrainGenerator;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Core.Director;
import GameEngine.GameObjects.Core.Monster;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.GameObjects.MainMenu.MenuSelector;
import GameEngine.GameObjects.RandomMove;
import GameEngine.Levels.Level;

import java.util.Random;

import static GameEngine.Levels.PlayLevel.DESIRED_WALLS;


public class MainMenu extends Level {
   // Attributes
   public static final String BACKGROUND_LEVEL = "menu_map.json";
   public Level advance = null;

   // Constructor
   public MainMenu(GameEngine sys) {
      super(sys);
   }

   // Methods
   public void drawBackground(){
      sys.background(0);
   }

   public void start() {
      // Init world
      init_terrain();

      sys.spawn(new MenuSelector(sys), 3);

      RandomMove random_move = new RandomMove(sys);

      sys.chase_position = random_move.pos;

      float desired_x_zoom = GameEngine.SCREEN_WIDTH / (DESIRED_WALLS * GameEngine.PIXEL_TO_METER);
      float desired_y_zoom = GameEngine.SCREEN_HEIGHT / (DESIRED_WALLS * GameEngine.PIXEL_TO_METER);

      sys.chase_zoom = Math.min(desired_x_zoom, desired_y_zoom);

      sys.spawn(random_move, 0);
   }

   public boolean updateAndCanAdvance() {
      return advance != null;
   }

   public Level advance() {
      return advance;
   }

   private void init_terrain(){
      Player.ACTIVE = false;
      Monster.ACTIVE = false;
      ForceManager.ACTIVE = false;

      int seed = new Random().nextInt();
      System.out.println(seed);

      sys.terrain = new Terrain(sys, seed, LoadedTerrainGenerator::new);
      var generator = sys.terrain.getComponent(LoadedTerrainGenerator.class);
      generator.loadTerrain(BACKGROUND_LEVEL);

      // Init world
      sys.initWorld(generator.getWidth(), generator.getHeight());
      sys.spawn(sys.terrain, 0);

      // Create player
      Player player = new Player(sys, generator.player_spawn_loc.copy(), false);
      sys.spawn(player, 2);

      // Create director
      Director director = new Director(sys, player, false);
      sys.spawn(director, 0);

      generator.spawn_monsters();
      generator.spawn_goal();
   }
}

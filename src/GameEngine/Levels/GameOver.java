package GameEngine.Levels;

import GameEngine.Components.TerrianComponents.LoadedTerrainGenerator;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.GameObjects.LeaderBoard;
import GameEngine.GameObjects.MainMenu.LevelSelector;
import GameEngine.Utils.Managers.InputManager;


public class GameOver extends Level{
   // Attributes
   private InputManager.Key menu;
   private Level advance;

   private final String file_name;
   private final LoadedTerrainGenerator generator;
   private final float time;

   // Constructor
   public GameOver(GameEngine sys, String file_name, float time) {
      super(sys);

      // Init attributes
      this.file_name = file_name;
      this.time = time;

      var terrain = new Terrain(sys, 0, LoadedTerrainGenerator::new);
      generator = terrain.getComponent(LoadedTerrainGenerator.class);
      generator.loadTerrain(file_name);
   }

   // Methods
   public void drawBackground(){
      sys.background(127);
   }


   public void start() {
      // Get restart keys
      menu = sys.input_manager.getKey("EXIT_TO_MENU");
      sys.initWorld(1, 1);

      // Spawn objects
      sys.spawn(new LeaderBoard(sys, file_name, generator), 1);
   }

   public void menu_pressed(){
      advance = new MainMenu(sys);
   }


   public boolean updateAndCanAdvance() {
      if(menu.pressed) {
         advance = new MainMenu(sys);
      }

      return advance != null;
   }

   public Level advance() {
      return advance;
   }
}

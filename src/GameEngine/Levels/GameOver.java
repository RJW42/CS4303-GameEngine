package GameEngine.Levels;

import GameEngine.Components.TerrianComponents.LoadedTerrainGenerator;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.GameObjects.LeaderBoard;
import GameEngine.GameObjects.MainMenu.LevelSelector;
import GameEngine.GameObjects.TimeSaver;
import GameEngine.Utils.Managers.InputManager;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import static GameEngine.Components.TerrianComponents.TerrainLoader.SAVE_LOC;


public class GameOver extends Level{
   // Attributes
   private InputManager.Key menu;
   private Level advance;

   private final String file_name;
   public final String next_level;
   private final LoadedTerrainGenerator generator;
   private final float time;

   // Constructor
   public GameOver(GameEngine sys, String file_name, float time) {
      super(sys);

      File folder = new File(SAVE_LOC);
      File[] files = folder.listFiles();
      Arrays.sort(files, new Comparator<File>() {
         public int compare(File f1, File f2){
            return f1.getName().compareTo(f2.getName());
         }
      });

      String next_level = null;
      boolean is_next = false;

      for(var f : files){
         if(f.getName().equals(file_name)){
            is_next = true;
         }else if(is_next){
            next_level = f.getName();
            break;
         }
      }

      // Init attributes
      this.next_level = next_level;
      this.file_name = file_name;
      this.time = time;

      var terrain = new Terrain(sys, 0, LoadedTerrainGenerator::new);
      generator = terrain.getComponent(LoadedTerrainGenerator.class);
      generator.loadTerrain(file_name);
   }

   // Methods
   public void drawBackground(){
      sys.background(127, 159, 159);
   }


   public void start() {
      // Get restart keys
      menu = sys.input_manager.getKey("EXIT_TO_MENU");
      sys.initWorld(1, 1);

      // Spawn objects
      sys.spawn(new TimeSaver(sys, generator, file_name, time), 2);
   }

   public void menu_pressed(){
      advance = new MainMenu(sys);
   }

   public void next_level(){
      advance = new PlayLevel(sys, next_level);
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

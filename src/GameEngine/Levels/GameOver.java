package GameEngine.Levels;

import GameEngine.GameEngine;
import GameEngine.Utils.Managers.InputManager;


public class GameOver extends Level{
   // Attributes
   private InputManager.Key menu;
   private Level advance;

   private final String file_name;
   private final float time;

   // Constructor
   public GameOver(GameEngine sys, String file_name, float time) {
      super(sys);

      // Init attributes
      this.file_name = file_name;
      this.time = time;
   }

   // Methods
   public void drawBackground(){
      sys.background(0);
   }


   public void start() {
      // Get restart keys
      menu = sys.input_manager.getKey("EXIT_TO_MENU");
      sys.initWorld(1, 1);
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

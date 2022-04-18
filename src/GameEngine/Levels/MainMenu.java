package GameEngine.Levels;

import GameEngine.GameEngine;


public class MainMenu extends Level{
   // Attributes
   boolean stop = false;

   // Constructor
   public MainMenu(GameEngine sys) {
      super(sys);
   }

   // Methods
   public void drawBackground(){
      sys.background(127);
   }

   public void start() {
      // Init world
      sys.initWorld(1, 1);

      // Todo: add selection for:
      //       play_level
      //       create_level
      //       controls
      //       maybe settings like sound
   }

   public boolean updateAndCanAdvance() {
      return false;
   }

   public Level advance() {
      return null;
   }
}

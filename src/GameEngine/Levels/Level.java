package GameEngine.Levels;

import GameEngine.GameEngine;

public abstract class Level {
   // Attributes
   protected GameEngine sys;

   // Constructor
   protected Level(GameEngine sys){
      this.sys = sys;
   }

   // Methods
   public abstract void drawBackground();
   public abstract void start();
   public abstract boolean updateAndCanAdvance();
   public abstract Level advance();
}

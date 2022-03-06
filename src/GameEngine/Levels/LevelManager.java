package GameEngine.Levels;

import GameEngine.GameEngine;

public class LevelManager{
   // Attributes
   private Level current;
   private GameEngine sys;

   // Constructor
   public LevelManager(GameEngine sys, Level start_level) {
      this.sys = sys;

      // Start First Level
      this.startLevel(start_level);
   }

   // Methods
   public void update(){
      // Check if we can change level
      if(this.current.updateAndCanAdvance()){
         // Add vance to new level
         this.startLevel(current.advance());
      }
   }

   public void drawBackground(){
      current.drawBackground();
   }

   public void startLevel(Level level){
      // Set New Current
      current = level;

      // Start new level
      level.start();
   }
}

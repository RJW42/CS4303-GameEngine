package GameEngine.Levels;

import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Ball;

public class TestLevel extends Level{
   // Attributes

   // Constructor
   public TestLevel(GameEngine sys) {
      super(sys);
   }

   // Methods
   public void drawBackground(){
      sys.background(0);
   }

   public void start() {
      // Init world
      sys.initWorld();
   }

   public boolean updateAndCanAdvance() {
      return false;
   }

   public Level advance() {
      return null;
   }
}

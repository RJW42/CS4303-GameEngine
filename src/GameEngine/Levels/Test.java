package GameEngine.Levels;

import GameEngine.Components.AIComponents.Timer;
import GameEngine.Components.TerrianComponents.LoadedTerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainLoader;
import GameEngine.Components.TerrianComponents.TerrainRenderer;
import GameEngine.GameObjects.Core.Director;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Test.Pointer;
import GameEngine.GameObjects.Test.TestTerrian;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PVector;

import java.util.Random;


public class Test extends Level{
   // Attributes

   // Constructor
   public Test(GameEngine sys) {
      super(sys);

      // Init attributes
   }

   // Methods
   public void drawBackground(){
      sys.background(0);
   }


   public void start() {
      // Get restart keys
      sys.initWorld(20, 20);
      sys.spawn(new Pointer(sys), 2);
      //sys.spawn(new Player(sys, new PVector(2, 2)), 2);
      sys.spawn(new TestTerrian(sys, new PVector(5, 5)), 1);
      sys.spawn(new TestTerrian(sys, new PVector(5, 6)), 1);
      sys.spawn(new TestTerrian(sys, new PVector(5, 7)), 1);
   }




   public boolean updateAndCanAdvance() {
      return false;
   }

   public Level advance() {
      return null;
   }


}

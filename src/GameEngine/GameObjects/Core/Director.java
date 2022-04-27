package GameEngine.GameObjects.Core;


import GameEngine.Components.AIComponents.AIMovement.AIPathManager;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;


public class Director extends GameObject {
   // Attributes
   public boolean is_dead = false;


   // Constructor
   public Director(GameEngine sys) {
      super(sys);

      // Add regular components 
      this.components.add(new AIPathManager(this));
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}

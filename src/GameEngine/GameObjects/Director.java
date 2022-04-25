package GameEngine.GameObjects;


import GameEngine.Components.AIComponents.AIMovement.AIPathManager;
import GameEngine.GameEngine;


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

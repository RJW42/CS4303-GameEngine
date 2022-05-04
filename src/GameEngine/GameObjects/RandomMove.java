package GameEngine.GameObjects;


import GameEngine.Components.RandomMover;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Core.Terrain;
import processing.core.PVector;


public class RandomMove extends GameObject {
   // Attributes
   public boolean is_dead = false;


   // Constructor
   public RandomMove(GameEngine sys) {
      super(sys);

      this.pos = new PVector(Terrain.WIDTH / 2f, Terrain.HEIGHT / 2f);

      // Add regular components 
      this.components.add(new RandomMover(this));
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}

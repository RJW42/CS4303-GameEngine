package GameEngine.GameObjects;


import GameEngine.Components.Renderers.TextRenderer;
import GameEngine.GameEngine;
import processing.core.PVector;


public class PressStart extends GameObject {
   // Attributes
   public boolean is_dead = false;


   // Constructor
   public PressStart(GameEngine sys, PVector spawn_location) {
      super(sys);

      this.pos = spawn_location;

      // Add regular components 
      this.components.add(new TextRenderer(this, new PVector(0, 0)));
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}

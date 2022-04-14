package GameEngine.GameObjects;


import GameEngine.Components.MapBuildingComponents.NoClipController;
import GameEngine.GameEngine;
import processing.core.PVector;


public class MapBuilderController extends GameObject {
   // Attributes
   public static final float SPEED = 20f;

   public boolean is_dead = false;


   // Constructor
   public MapBuilderController(GameEngine sys, PVector pos) {
      super(sys);

      this.pos = pos;

      // Add regular components 
      this.components.add(new NoClipController(this, SPEED));
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}

package GameEngine.GameObjects;


import GameEngine.Components.Renderers.CircleRenderer;
import GameEngine.GameEngine;
import processing.core.PVector;


public class Pointer extends GameObject {
   // Attributes
   public boolean is_dead = false;


   // Constructor
   public Pointer(GameEngine sys) {
      super(sys);

      this.pos = new PVector();

      // Add regular components 
      this.components.add(new CircleRenderer(this, new PVector(255, 0, 0), 0.5f));
   }


   // Methods


   @Override
   public void update() {
      pos.x = sys.mouse_x;
      pos.y = sys.mouse_y;
      super.update();
   }

   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}

package GameEngine.Components;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;


public class ForceManager extends Component {
   // Attributes
   public PVector velocity;

   // Constructor
   public ForceManager(GameObject parent, PVector start_velocity) {
      super(parent);

      // Init attributes
      this.velocity = start_velocity;
   }


   // Methods 
   public void start(){
   }

   public void update() {
      /// Update parents position
      parent.pos.add(PVector.mult(velocity, sys.DELTA_TIME));
   }

   public void draw() {
   }
}

package GameEngine.Components;


import GameEngine.GameObjects.GameObject;
import processing.core.PVector;


public class ForceManager extends Component {
   // Attributes
   public PVector velocity;
   public PVector acceleration;

   // Constructor
   public ForceManager(GameObject parent, PVector start_velocity, PVector start_acceleration) {
      super(parent);

      // Todo: could take in attributes such as a speed and a force limit

      // Init attributes
      this.velocity = start_velocity;
      this.acceleration = start_acceleration;
   }


   // Methods
   public void applyForce(PVector force){
      acceleration.add(force);
   }

   public void start(){

   }

   public void update() {
      // Update velocity and position
      velocity.add(PVector.mult(acceleration, sys.DELTA_TIME));
      parent.pos.add(PVector.mult(velocity, sys.DELTA_TIME));
   }

   public void draw() {

   }
}

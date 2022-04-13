package GameEngine.Components;


import GameEngine.GameObjects.GameObject;
import processing.core.PVector;


public class ForceManager extends Component {
   // Attributes
   public PVector velocity;
   public PVector acceleration;
   public boolean grounded;
   public boolean set_grounded;
   public boolean apply_friction;
   public float friction;

   private int frames_grounded = 0;


   // Constructor
   public ForceManager(GameObject parent, PVector start_velocity, PVector start_acceleration, float friction) {
      super(parent);

      // Todo: could take in attributes such as a speed and a force limit

      // Init attributes
      this.velocity = start_velocity;
      this.acceleration = start_acceleration;
      this.grounded = false;
      this.set_grounded = false;
      this.apply_friction = true;
      this.friction = friction;
   }


   // Methods
   public void applyForce(PVector force){
      acceleration.add(force);
   }

   public void start(){

   }

   public void update() {
      // Check if no longer grounded
      if(!set_grounded) {
         grounded = false;
         frames_grounded = 0;
      } else {
         frames_grounded++;
      }

      // Update velocity and position
      velocity.add(PVector.mult(acceleration, sys.DELTA_TIME));
      parent.pos.add(PVector.mult(velocity, sys.DELTA_TIME));

      // Add gravity
      velocity.y -= sys.GRAVITY * sys.DELTA_TIME;

      // Add friction
      if(apply_friction && frames_grounded > 1){
         System.out.println(velocity.x);

         if(Math.abs(velocity.x) < 0.05)
            velocity.x = 0;

         velocity.x -= velocity.x * friction;
      }


      // Reset acceleration
      acceleration.x = 0;
      acceleration.y = 0;

      // Update grounded check
      set_grounded = false;
   }

   public void draw() {

   }
}

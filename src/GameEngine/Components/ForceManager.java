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
   public boolean apply_gravity;
   public float friction;

   public PVector grapple_base;
   public float grapple_length;

   private int frames_grounded = 0;


   // Constructor
   public ForceManager(GameObject parent, PVector start_velocity, PVector start_acceleration, float friction) {
      this(parent, start_velocity, start_acceleration, friction, true);
   }

   public ForceManager(GameObject parent, PVector start_velocity, PVector start_acceleration, float friction, boolean has_gravity) {
      super(parent);

      // Todo: could take in attributes such as a speed and a force limit

      // Init attributes
      this.velocity = start_velocity;
      this.acceleration = start_acceleration;
      this.grounded = false;
      this.set_grounded = false;
      this.apply_friction = true;
      this.friction = friction;
      this.apply_gravity = has_gravity;
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

      if(grapple_base != null)
         apply_pendulum(); // Apply grapple physics if needed

      // Add frictions and gravity
      update_velocities();

      // Update grounded check
      set_grounded = false;
   }


   private void apply_pendulum(){
      PVector direction_to_base = PVector.sub(grapple_base, parent.pos).normalize();
      float curr_dist_to_base = PVector.dist(grapple_base, parent.pos);
      float speed_to_base = PVector.dot(velocity, direction_to_base);

      if(speed_to_base >= 0 || curr_dist_to_base <= grapple_length) {
         return; // Moving towards base, so do not want to apply grapple physics
      }

      velocity.sub(PVector.mult(direction_to_base, speed_to_base));
      PVector new_pos = PVector.sub(grapple_base, PVector.mult(direction_to_base, grapple_length));

      if(new_pos.equals(parent.pos)){
         return; // New position t0o similar to old, so use velocity to update
      }

      parent.pos.x = new_pos.x;
      parent.pos.y = new_pos.y;
   }


   private void update_velocities(){
      // Add gravity
      if(apply_gravity) velocity.y -= sys.GRAVITY * sys.DELTA_TIME;

      // Add friction
      if(apply_friction && frames_grounded > 1){
         if(Math.abs(velocity.x) < 0.05)
            velocity.x = 0;

         velocity.x -= velocity.x * friction;
      }

      // Reset acceleration
      acceleration.x = 0;
      acceleration.y = 0;
   }


   public void draw() {

   }
}

package GameEngine.Components.PlayerComponents;

import GameEngine.Components.Component;
import GameEngine.Components.ForceManager;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PVector;

public class CharacterController extends Component {
   // Attributes
   public float speed;
   public float max_speed;
   public float reel_distance = 3;
   public float reel_speed    = 1.5f;

   private ForceManager force_manager;
   private GrappleHook grapple_hook;
   private InputManager.Key reel_in;
   private InputManager.Key reel_out;
   private InputManager.Key jump;
   private InputManager.Key left;
   private InputManager.Key right;

   private int double_jump_count = 0;
   private boolean can_double_jump = false;
   private boolean can_jump = false;

   // Constructor
   public CharacterController(GameObject parent, float speed, float max_speed) {
      super(parent);

      // Init attributes
      this.speed  = speed;
      this.max_speed = max_speed;
      this.reel_in   = sys.input_manager.getKey("grapple_in");
      this.reel_out   = sys.input_manager.getKey("grapple_out");
      this.jump   = sys.input_manager.getKey("jump");
      this.left   = sys.input_manager.getKey("left");
      this.right  = sys.input_manager.getKey("right");
   }

   // Methods
   @Override
   public void start() {
      this.force_manager = parent.getComponent(ForceManager.class);
      this.grapple_hook = parent.getComponent(GrappleHook.class);
   }

   public void update() {
      if(!Player.ACTIVE) return;

      jump_check();
      update_velocity();
   }

   private void update_velocity(){
      // Jumping
      if(jump.pressed && can_jump){
         force_manager.velocity.y = 8;
      }

      // Grapple hook
      if(grapple_hook.fired && reel_in.pressed){
         float distance = reel_distance * sys.DELTA_TIME;
         if(force_manager.grapple_length >= 0.25f) {
            force_manager.grapple_length -= distance;
            force_manager.applyForce(PVector.sub(force_manager.grapple_base, parent.pos).normalize().mult(reel_distance));
         }
      }else if(grapple_hook.fired && reel_out.pressed) {
         force_manager.grapple_length += reel_distance * sys.DELTA_TIME;
         parent.pos.add(PVector.sub(force_manager.grapple_base, parent.pos).normalize().mult(-reel_distance * sys.DELTA_TIME));
      }

      // Left right movement
      if(left.pressed){
         if(force_manager.velocity.x > 0)
            force_manager.velocity.x = 0;
         if(force_manager.velocity.x > -max_speed)
            force_manager.applyForce(new PVector(-speed, 0));
      } else if(right.pressed){
         if(force_manager.velocity.x < 0)
            force_manager.velocity.x = 0;
         if(force_manager.velocity.x < max_speed)
            force_manager.applyForce(new PVector(speed, 0));
      }
   }


   private void jump_check(){
      // Check if the player can jump
      if(force_manager.grounded){
         can_jump = true;
         can_double_jump = false;
         double_jump_count = 0;
         return;
      }

      can_jump = false;

      if(!jump.pressed){
         can_double_jump = true;
         return;
      }

      if(can_double_jump && double_jump_count++ < 1){
         can_jump = true;
      }
   }
}

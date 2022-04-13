package GameEngine.Components.PlayerComponents;

import GameEngine.Components.Component;
import GameEngine.Components.ForceManager;
import GameEngine.Components.Renderers.ImageRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PVector;

import java.util.List;

public class CharacterController extends Component {
   // Attributes
   public float speed;
   public float max_speed;

   private ForceManager force_manager;
   private InputManager.Key up;
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
      this.up     = sys.input_manager.getKey("jump");
      this.left   = sys.input_manager.getKey("left");
      this.right  = sys.input_manager.getKey("right");
   }

   // Methods
   @Override
   public void start() {
      this.force_manager = parent.getComponent(ForceManager.class);
   }

   public void update() {
      jump_check();
      update_velocity();
   }

   private void update_velocity(){
      if(up.pressed && can_jump){
         force_manager.velocity.y = 8;
      }

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

      if(!up.pressed){
         can_double_jump = true;
         return;
      }

      if(can_double_jump && double_jump_count++ < 1){
         can_jump = true;
      }
   }
}

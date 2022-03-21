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

   private ForceManager force_manager;
   private InputManager.Key up;
   private InputManager.Key down;
   private InputManager.Key left;
   private InputManager.Key right;

   // Constructor
   public CharacterController(GameObject parent, float speed) {
      super(parent);

      // Init attributes
      this.speed  = speed;
      this.up     = sys.input_manager.getKey("up");
      this.down   = sys.input_manager.getKey("down");
      this.left   = sys.input_manager.getKey("left");
      this.right  = sys.input_manager.getKey("right");
   }

   // Methods
   @Override
   public void start() {
      this.force_manager = parent.getComponent(ForceManager.class);
   }

   public void update() {
      updateVelocity();
   }

   private void updateVelocity(){
      if(up.pressed){
         force_manager.velocity.y = speed;
      } else if(down.pressed){
         force_manager.velocity.y = -speed;
      } else {
         force_manager.velocity.y = 0;
      }

      if(left.pressed){
         force_manager.velocity.x = -speed;
      } else if(right.pressed){
         force_manager.velocity.x = speed;
      } else {
         force_manager.velocity.x = 0;
      }
   }
}

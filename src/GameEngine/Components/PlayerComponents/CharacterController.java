package GameEngine.Components.PlayerComponents;

import GameEngine.Components.Component;
import GameEngine.Components.ForceManager;
import GameEngine.Components.Renderers.ImageRenderer;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

import java.util.List;

public class CharacterController extends Component {
   // Attributes
   public static final int FORWARD     = 87;
   public static final int BACKWARDS   = 83;
   public static final int LEFT        = 65;
   public static final int RIGHT       = 68;
   public float speed;

   private ForceManager force_manager;

   // Constructor
   public CharacterController(GameObject parent, float speed) {
      super(parent);
      this.speed = speed;
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
      if(sys.input_manager.keys_pressed[FORWARD]){
         force_manager.velocity.y = speed;
      } else if(sys.input_manager.keys_pressed[BACKWARDS]){
         force_manager.velocity.y = -speed;
      } else {
         force_manager.velocity.y = 0;
      }

      if(sys.input_manager.keys_pressed[LEFT]){
         force_manager.velocity.x = -speed;
      } else if(sys.input_manager.keys_pressed[RIGHT]){
         force_manager.velocity.x = speed;
      } else {
         force_manager.velocity.x = 0;
      }
   }
}

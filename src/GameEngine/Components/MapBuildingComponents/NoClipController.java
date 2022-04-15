package GameEngine.Components.MapBuildingComponents;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.Managers.InputManager;


public class NoClipController extends Component {
   // Attributes
   public float speed;

   private InputManager.Key up;
   private InputManager.Key left;
   private InputManager.Key right;
   private InputManager.Key down;


   // Constructor
   public NoClipController(GameObject parent, float speed) {
      super(parent);
      this.speed = speed;
   }


   // Methods 
   public void start() {
      this.up     = sys.input_manager.getKey("mb-up");
      this.down   = sys.input_manager.getKey("mb-down");
      this.left   = sys.input_manager.getKey("mb-left");
      this.right  = sys.input_manager.getKey("mb-right");
   }

   public void update() {
      if(this.up.pressed)
         parent.pos.y += speed * sys.DELTA_TIME;
      else if(this.down.pressed)
         parent.pos.y -= speed * sys.DELTA_TIME;

      if(this.right.pressed)
         parent.pos.x += speed * sys.DELTA_TIME;
      else if(this.left.pressed)
         parent.pos.x -= speed * sys.DELTA_TIME;
   }

   public void draw() {

   }
}

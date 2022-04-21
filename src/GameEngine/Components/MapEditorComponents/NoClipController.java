package GameEngine.Components.MapEditorComponents;


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
   private InputManager.Key zoom_in;
   private InputManager.Key zoom_out;



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
      this.zoom_in   = sys.input_manager.getKey("mb-zoom-in");
      this.zoom_out  = sys.input_manager.getKey("mb-zoom-out");
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

      if(this.zoom_in.pressed)
         sys.chase_zoom += 0.1f;
      else if(this.zoom_out.pressed)
         sys.chase_zoom -= sys.chase_zoom > 1 ? 0.1f : 0;
   }

   public void draw() {

   }
}

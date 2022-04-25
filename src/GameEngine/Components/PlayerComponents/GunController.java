package GameEngine.Components.PlayerComponents;


import GameEngine.Components.Component;
import GameEngine.Components.Weapons.Gun;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PVector;


public class GunController extends Component {
   // Attributes
   private InputManager.Key fire;
   private Gun gun; // Todo:make list

   // Constructor
   public GunController(GameObject parent) {
      super(parent);

      // Init attributes
      this.fire = sys.input_manager.getKey("fire");
   }


   // Methods 
   public void start() {
      // Todo: get all guns
      this.gun = parent.getComponent(Gun.class);
   }

   public void update() {
      // Update guns position
      gun.target_pos.x = sys.mouse_x;
      gun.target_pos.y = sys.mouse_y;

      // Fire gun
      if(fire.pressed)
         gun.fire();
   }

   public void draw() {
   }
}

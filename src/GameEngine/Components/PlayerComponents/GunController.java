package GameEngine.Components.PlayerComponents;


import GameEngine.Components.Component;
import GameEngine.Components.Weapons.Gun;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PVector;

import java.util.List;


public class GunController extends Component {
   // Attributes
   private InputManager.Key fire;
   private InputManager.Key switch_weapon;
   private Gun active_gun;
   private int active_index;
   private List<Gun> guns;
   private boolean can_switch;

   // Constructor
   public GunController(GameObject parent) {
      super(parent);

      // Init attributes
      this.fire = sys.input_manager.getKey("fire");
      this.switch_weapon = sys.input_manager.getKey("switch_weapon");
      this.can_switch = true;
   }


   // Methods 
   public void start() {
      // Get all guns
      guns = parent.getComponents(Gun.class);
      active_gun = guns.get(0);
      active_index = 0;

      guns.forEach(g -> g.active = false);
      active_gun.active = true;
   }

   public void update() {
      // Check if active
      if(!Player.ACTIVE) return;

      // Update guns position
      active_gun.target_pos.x = sys.mouse_x;
      active_gun.target_pos.y = sys.mouse_y;

      // Fire gun
      if(fire.pressed) active_gun.fire();

      // Swtich gun
      if(switch_weapon.pressed && can_switch) {
         active_index = (active_index + 1) % guns.size();
         active_gun.active = false;
         active_gun = guns.get(active_index);
         active_gun.active = true;
         can_switch = false;
      }

      if(!switch_weapon.pressed) can_switch = true;
   }

   public void draw() {
      // Todo: draw active gun
   }
}

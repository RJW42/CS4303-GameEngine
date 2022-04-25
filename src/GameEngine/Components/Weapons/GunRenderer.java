package GameEngine.Components.Weapons;

import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

public abstract class GunRenderer extends Component {
   // Attributes
   public Gun gun;
   public PVector offset;

   // Constructor
   public GunRenderer(GameObject parent, PVector offset) {
      super(parent);

      // Init attributes
      this.offset = offset;
   }


   // Methods
   public void fire(){

   }
}

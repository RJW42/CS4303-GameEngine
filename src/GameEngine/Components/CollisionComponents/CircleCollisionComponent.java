package GameEngine.Components.CollisionComponents;

import GameEngine.GameObjects.GameObject;
import GameEngine.Triggers.CollisionTrigger;
import processing.core.PVector;

public class CircleCollisionComponent extends BaseCollisionComponent{
   // Attributes
   public float rad;

   // Constructor
   public CircleCollisionComponent(GameObject parent, CollisionTrigger trigger, float rad) {
      this(parent, trigger, new PVector(-rad, rad), rad);
   }

   public CircleCollisionComponent(GameObject parent, CollisionTrigger trigger, PVector offset, float rad) {
      super(parent, trigger, offset, (rad * 2), (rad * 2));
      this.rad = rad;
      this.trigger = trigger;
   }

   // Methods

   @Override
   public void draw() {
      if(sys.DISPLAY_BOUNDS){
         sys.noFill();
         sys.stroke(0, 0, 255);
         sys.circle(get_x() + rad, get_y() - rad, rad * 2);
      }
   }
}

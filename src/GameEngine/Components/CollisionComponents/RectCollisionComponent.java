package GameEngine.Components.CollisionComponents;

import GameEngine.GameObjects.GameObject;
import GameEngine.Triggers.CollisionTrigger;
import processing.core.PVector;

public class RectCollisionComponent extends BaseCollisionComponent{
   // Attributes
   public float width;
   public float height;

   // Constructor
   public RectCollisionComponent(GameObject parent, CollisionTrigger trigger, float width) {
      this(parent, trigger, width, width);
   }

   public RectCollisionComponent(GameObject parent, CollisionTrigger trigger, float width, float height) {
      this(parent, trigger, new PVector(0, 0), width, height);
   }

   public RectCollisionComponent(GameObject parent, CollisionTrigger trigger, PVector offset, float width, float height) {
      super(parent, trigger, offset, width, height);
      this.width = width;
      this.height = height;
      this.trigger = trigger;
   }

   // Methods

   @Override
   public void draw() {
      if(sys.DISPLAY_BOUNDS){
         sys.stroke(0, 255, 0);
         sys.noFill();
         sys.rect(get_x(), get_y() - height, width, height);
         sys.fill(255);
         sys.circle(get_x(), get_y(), 0.1f);
      }
   }
}

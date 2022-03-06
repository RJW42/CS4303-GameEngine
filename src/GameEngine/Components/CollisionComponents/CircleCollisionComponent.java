package GameEngine.Components.CollisionComponents;

import GameEngine.GameObjects.GameObject;
import GameEngine.Triggers.CollisionTrigger;
import processing.core.PVector;

public class CircleCollisionComponent extends BaseCollisionComponent{
   // Attributes
   public float rad;

   // Constructor
   public CircleCollisionComponent(GameObject parent, CollisionTrigger trigger, float rad) {
      this(parent, trigger, new PVector(0, 0), rad);
   }

   public CircleCollisionComponent(GameObject parent, CollisionTrigger trigger, PVector offset, float rad) {
      super(parent, trigger, offset, (int)(rad * 2),  (int)(rad * 2));
      this.rad = rad;
      this.trigger = trigger;
   }

   // Methods
   @Override
   public void update() {

   }

   @Override
   public void draw() {

   }
}

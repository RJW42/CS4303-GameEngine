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
      super(parent, trigger, offset, (int)width, (int)height);
      this.width = width;
      this.height = height;
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

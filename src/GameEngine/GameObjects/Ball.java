package GameEngine.GameObjects;

import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.GameEngine;
import GameEngine.Triggers.CollisionTrigger;

public class Ball extends GameObject implements CollisionTrigger {
   // Attributes
   CircleCollisionComponent coll_component;

   // Constructor
   public Ball(GameEngine sys) {
      super(sys);
      this.coll_component = new CircleCollisionComponent(this, this, 5f);

      this.components.add(coll_component);
   }

   // Methods
   @Override
   public boolean isDestroyed() {
      return false;
   }

   @Override
   public boolean resolveCollision(BaseCollisionComponent other) {
      return false;
   }
}

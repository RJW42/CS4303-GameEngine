package GameEngine.Triggers;

import GameEngine.Components.CollisionComponents.BaseCollisionComponent;

public interface CollisionTrigger {
   public boolean resolveCollision(BaseCollisionComponent other);
}

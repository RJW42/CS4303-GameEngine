package GameEngine.Components.CollisionComponents;

import java.util.List;

public interface Collideable {
   public List<BaseCollisionComponent> getCollisionComponents();
   public BaseCollisionComponent getCollisionComponent();
}

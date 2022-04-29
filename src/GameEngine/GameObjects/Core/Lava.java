package GameEngine.GameObjects.Core;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.Damagable;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Lava extends GameObject implements Collideable {
   // Attributes
   public boolean is_dead = false;

   public Terrain terrain;
   public ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public Lava(GameEngine sys, Terrain terrain) {
      super(sys);

      // init attributes
      this.terrain = terrain;

      // Add collision components 
      this.collision_components = new ArrayList<>();
      this.pos = new PVector(0, 0);
   }

   public boolean on_collision(BaseCollisionComponent other){
      if(!(other.parent instanceof Monster || other.parent instanceof Player)) return false;

      // Singal to the object they have collided with lava
      other.parent.getComponent(Damagable.class).lava();

      return true;
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   @Override
   public List<BaseCollisionComponent> getCollisionComponents() {
      return collision_components;
   }

   @Override
   public BaseCollisionComponent getCollisionComponent() {
      return collision_components.get(0);
   }
}

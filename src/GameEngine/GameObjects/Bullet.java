package GameEngine.GameObjects;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.AIComponents.Damageable;
import GameEngine.Components.ForceManager;
import GameEngine.Components.Renderers.CircleRenderer;
import GameEngine.GameEngine;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Bullet extends GameObject implements Collideable {
   // Attributes
   public static final PVector COLOUR = new PVector(0, 0, 0);

   public boolean is_dead = false;
   public float momentum;
   public GameObject parent;

   private final ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public Bullet(GameEngine sys, GameObject parent, PVector spawn_location, PVector start_vel, float radius, float mass) {
      super(sys);

      // Init spawn location
      this.pos = spawn_location;

      // Save parent. Prevents self dammage
      this.parent = parent;

      // Calc bullet momentum
      this.momentum = mass * start_vel.mag();

      // Add collision components 
      this.collision_components = new ArrayList<>();
      this.collision_components.add(new CircleCollisionComponent(this, this::onCollision, new PVector(-radius, radius), radius));

      // Add regular components 
      this.components.add(new ForceManager(this, start_vel));
      this.components.add(new CircleRenderer(this, COLOUR, radius));

      // Add collision components to regular
      this.components.addAll(this.collision_components);
   }


   // Methods
   public boolean onCollision(BaseCollisionComponent other){
      // Check if collided with wall
      if(other.parent instanceof Terrain || (other.parent instanceof Bullet && ((Bullet) other.parent).parent != parent)){
         is_dead = true;
      }

      if(other.parent == parent)
         return true; // Do not interact with same parent. Prevents self fire

      // Check if the other can be hurt by bullets
      Damageable damageable = other.parent.getComponent(Damageable.class);
      if(damageable != null) {
         // The other object can be hurt by bullets.
         damageable.hitByBullet(momentum);
         is_dead = true;
      }

      return true;
   }


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

package GameEngine.GameObjects.Bullets;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.Damagable;
import GameEngine.Components.ForceManager;
import GameEngine.Components.Weapons.ExplosionManager;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Explosion extends GameObject implements Collideable {
   // Attributes
   public static final float EXPLOSION_SPEED = 0.15f;
   public static final float MAX_EXPLOSION_RAD = 2f;

   public boolean is_dead = false;
   public float explosion_size;
   public GameObject parent;

   private final ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public Explosion(GameEngine sys, GameObject parent, PVector spawn_loc, float explosion_size) {
      super(sys);

      // Init attributes
      this.pos = spawn_loc;
      this.explosion_size = explosion_size;
      this.parent = parent;

      // Add collision components 
      this.collision_components = new ArrayList<>();
      this.collision_components.add(new CircleCollisionComponent(this, this::on_collision, new PVector(), 0.1f));

      // Add regular components 
      this.components.add(new ExplosionManager(this, EXPLOSION_SPEED, MAX_EXPLOSION_RAD));


      // Add collision components to regular
      this.components.addAll(this.collision_components);
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   public boolean on_collision(BaseCollisionComponent other){
      // Damage the other object if possible
      if(other.parent != parent) {
         Damagable damagable = other.parent.getComponent(Damagable.class);
         if (damagable != null) damagable.blast(this);
      }

      // Check if the other object has a force manager
      if(other.parent instanceof Player){
         ForceManager force_manager = other.parent.getComponent(ForceManager.class);
         force_manager
                 .applyForce(PVector
                         .sub(PVector.add(other.parent.pos, new PVector(Player.COLLISION_WIDTH / 2f, - Player.COLLISION_HEIGHT / 2f)), pos)
                 .normalize().mult(explosion_size));
      }
      return true;
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

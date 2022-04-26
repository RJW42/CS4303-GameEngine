package GameEngine.GameObjects;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.ForceManager;
import GameEngine.Components.Renderers.CircleRenderer;
import GameEngine.GameEngine;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Bullet extends GameObject implements Collideable {
   // Attributes
   public boolean is_dead = false;
   public GameObject parent;
   public float momentum;

   private ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public Bullet(GameEngine sys, GameObject parent, PVector start_vel, PVector spawn_location, float radius, float mass) {
      super(sys);

      // Init Attributes
      this.parent = parent;
      this.pos = spawn_location;
      this.momentum = mass * start_vel.mag();

      // Add collision components 
      this.collision_components = new ArrayList<>();
      this.collision_components.add(new CircleCollisionComponent(this, this::onCollision, new PVector(-radius, radius), radius));

      // Add regular components 
      this.components.add(new ForceManager(this, start_vel, new PVector(0, 0), 0));
      this.components.add(new CircleRenderer(this, new PVector(255, 0, 0), radius));


      // Add collision components to regular
      this.components.addAll(this.collision_components);
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   public boolean onCollision(BaseCollisionComponent other){
      // Check if hit wall
      if(other.parent == sys.terrain || other.parent == parent) {
         // Todo: spawn explosion here if one is wanted
         is_dead = true;
         return true;
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

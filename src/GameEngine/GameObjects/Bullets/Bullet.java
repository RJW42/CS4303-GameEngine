package GameEngine.GameObjects.Bullets;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.ForceManager;
import GameEngine.Components.Renderers.CircleRenderer;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Bullet extends GameObject implements Collideable {
   // Attributes
   public boolean is_dead = false;
   public GameObject parent;
   public float momentum;
   public boolean explosive;
   public float explosion_size;

   private ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public Bullet(GameEngine sys, GameObject parent, PVector start_vel, PVector spawn_location, float radius, float mass, float explosion_size, boolean has_gravity) {
      super(sys);

      // Init Attributes
      this.parent = parent;
      this.pos = spawn_location;
      this.momentum = mass * start_vel.mag();
      this.explosion_size = explosion_size;
      this.explosive = explosion_size >= 0;

      // Add collision components 
      this.collision_components = new ArrayList<>();
      this.collision_components.add(new CircleCollisionComponent(this, this::onCollision, new PVector(-radius, radius), radius));

      // Add regular components
      this.components.add(new ForceManager(this, start_vel, new PVector(0, 0), 0, has_gravity));
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
      // Collided destroy bullet
      if(is_dead) return false;
      is_dead = true;

      // Todo: check if other is damagable, then change to only explode on terrain and damanagable maybe

      if(explosive) create_explosion();

      return true;
   }

   private void create_explosion(){
      System.out.println("yee");
      sys.spawn(new Explosion(sys, parent, pos.copy(), explosion_size), 2);
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

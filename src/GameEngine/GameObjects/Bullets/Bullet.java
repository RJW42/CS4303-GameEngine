package GameEngine.GameObjects.Bullets;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.Damagable;
import GameEngine.Components.ForceManager;
import GameEngine.Components.Renderers.CircleRenderer;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Core.Monster;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.GameObjects.GameObject;
import ddf.minim.AudioSample;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Bullet extends GameObject implements Collideable {
   // Attributes
   public static AudioSample HIT_MARKER      = null;

   public boolean is_dead = false;
   public GameObject parent;
   public float momentum;
   public boolean explosive;
   public float explosion_size;

   private final ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public Bullet(GameEngine sys, GameObject parent, PVector start_vel, PVector spawn_location, float radius, float mass, float explosion_size, boolean has_gravity) {
      super(sys);

      // Init Attributes
      this.parent = parent;
      this.pos = spawn_location;
      this.momentum = mass * start_vel.mag();
      this.explosion_size = explosion_size;
      this.explosive = explosion_size > 0;

      // Load sound if needed
      if(HIT_MARKER == null) {
         HIT_MARKER = sys.audio_manager.get_sample("hit_marker");
         HIT_MARKER.setGain(-20f);
      }

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
      if(!(other.parent instanceof Terrain || other.parent instanceof Monster || other.parent instanceof Player))
         return false;

      if(is_dead) return false;
      is_dead = true;

      if(other.parent instanceof Monster)
         HIT_MARKER.trigger();

      Damagable damagable = other.parent.getComponent(Damagable.class);
      if(damagable != null) damagable.shot(momentum);
      if(explosive) create_explosion();

      return true;
   }

   private void create_explosion(){
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

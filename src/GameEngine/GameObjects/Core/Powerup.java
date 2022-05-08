package GameEngine.GameObjects.Core;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.PlayerComponents.PowerupManager;
import GameEngine.Components.PlayerComponents.Powerups;
import GameEngine.Components.Renderers.ImageRenderer;
import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Powerup extends GameObject implements Collideable {
   // Attributes
   public static final float WIDTH = 0.5f;

   public boolean is_dead = false;
   public final Powerups type;

   private final ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public Powerup(GameEngine sys, PVector spawn_loc, Powerups type) {
      super(sys);

      this.pos = spawn_loc.copy();

      // Add collision components 
      this.collision_components = new ArrayList<>();
      this.collision_components.add(new RectCollisionComponent(this, this::on_collision, WIDTH, WIDTH));

      // Add regular components
      this.components.add(new ImageRenderer(this, type.sprite_name, WIDTH, WIDTH, new PVector()));

      // Init attributes
      this.type = type;

      // Add collision components to regular
      this.components.addAll(this.collision_components);
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   public boolean on_collision(BaseCollisionComponent comp){
      if(!(comp.parent instanceof Player))
         return false;

      comp.parent.getComponent(PowerupManager.class).collision(this);

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

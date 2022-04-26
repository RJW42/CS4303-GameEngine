package GameEngine.GameObjects.Core;


import GameEngine.Components.AIComponents.AIMovement.AIMovementController;
import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.ForceManager;
import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Monster extends GameObject implements Collideable {
   // Attributes
   public static final float COLLISION_HEIGHT= 0.75f;
   public static final float COLLISION_WIDTH = 0.5f;
   public static final float FRICTION        = 0.06f;

   public boolean is_dead = false;
   public ForceManager force_manager;

   private ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public Monster(GameEngine sys, PVector spawn_location) {
      super(sys);

      // Init attributes
      this.pos = spawn_location;

      this.force_manager = new ForceManager(
              this, new PVector(0, 0), new PVector(0, 0), FRICTION
      );

      // Add regular components
      this.components.add(new AIMovementController(this, new PVector(), 5));
      this.components.add(new RectRenderer(this, new PVector(255, 32, 32), COLLISION_WIDTH, COLLISION_HEIGHT));
      this.components.add(force_manager);


      // Add collision components
      this.collision_components = new ArrayList<>();

      RectCollisionComponent core = new RectCollisionComponent(this, null, COLLISION_WIDTH, COLLISION_HEIGHT);
      core.tag = BaseCollisionComponent.Tag.PRIMARY;

      this.collision_components.add(core);
      this.collision_components.add(new RectCollisionComponent(this, this::ground_collision, new PVector(0.05f, -(COLLISION_HEIGHT - 0f)), COLLISION_WIDTH - 0.1f, 0.05f));

      // Add collision components to regular
      this.components.addAll(this.collision_components);
   }



   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   private boolean ground_collision(BaseCollisionComponent comp){
      if(comp.parent != sys.terrain)
         return false;

      force_manager.grounded = true;
      force_manager.set_grounded = true;

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

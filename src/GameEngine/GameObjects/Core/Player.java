package GameEngine.GameObjects.Core;

import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.ForceManager;
import GameEngine.Components.PlayerComponents.*;
import GameEngine.Components.Renderers.CircleRenderer;
import GameEngine.Components.Renderers.ImageRenderer;
import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.Components.Weapons.MachineGun;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject implements Collideable {
   // Attributes
   public static final float COLLISION_HEIGHT= 0.75f;
   public static final float COLLISION_WIDTH = 0.5f;
   public static final float ACCELERATION    = 12f;
   public static final float MAX_SPEED       = 4f;
   public static final float FRICTION        = 0.06f;

   private final ArrayList<BaseCollisionComponent> collision_components;
   private ForceManager force_manager;

   public boolean is_dead = false;


   // Constructor
   public Player(GameEngine sys, PVector spawn_location) {
      super(sys);

      // Set position
      this.pos = spawn_location;

      // Add collision components
      this.collision_components = new ArrayList<>();

      RectCollisionComponent core = new RectCollisionComponent(this, null, COLLISION_WIDTH, COLLISION_HEIGHT);
      core.tag = BaseCollisionComponent.Tag.PRIMARY;

      this.collision_components.add(core);
      this.collision_components.add(new RectCollisionComponent(this, this::ground_collision, new PVector(0.05f, -(COLLISION_HEIGHT - 0f)), COLLISION_WIDTH - 0.1f, 0.05f));

      // Add regular components
      this.force_manager = new ForceManager(
              this, new PVector(0, 0), new PVector(0, 0), FRICTION
      );

      this.components.add(new RectRenderer(this, new PVector(0, 32, 128), COLLISION_WIDTH, COLLISION_HEIGHT));
      this.components.add(new CharacterController(this, ACCELERATION, MAX_SPEED));
      this.components.add(new GrappleHook(this));
      this.components.add(force_manager);
      this.components.add(new GunController(this));
      this.components.add(MachineGun.create(this, new PVector(COLLISION_WIDTH / 2f, -COLLISION_HEIGHT / 2f)));

      // Add collision components to regular
      this.components.addAll(this.collision_components);
   }

   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   private boolean ground_collision(BaseCollisionComponent comp){
      // Check if collided with ground
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

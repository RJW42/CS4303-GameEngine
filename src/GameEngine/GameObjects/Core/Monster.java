package GameEngine.GameObjects.Core;


import GameEngine.Components.AIComponents.AIMovement.AIMovementController;
import GameEngine.Components.AIComponents.Attacker;
import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.Damagable;
import GameEngine.Components.ForceManager;
import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import ddf.minim.AudioSample;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Monster extends GameObject implements Collideable {
   // Attributes
   public static boolean ACTIVE = true;

   public static final float COLLISION_HEIGHT= 0.75f;
   public static final float COLLISION_WIDTH = 0.5f;
   public static final float FRICTION        = 0.06f;
   public static final PVector COLOUR        = new PVector(255, 32, 32);
   public static final int HEALTH            = 50;

   public static final float PUNCH_FORCE     = 400f;
   public static final float PUNCH_DAMAGE    = 10f;
   public static final int PUNCH_RATE        = 1;

   public boolean is_dead = false;

   public final ForceManager force_manager;
   public final Damagable damagable;
   public final Attacker attacker;
   public final AIMovementController controller;
   private final ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public Monster(GameEngine sys, PVector spawn_location) {
      super(sys);

      // Init attributes
      this.pos = spawn_location;
      this.is_dead = true;

      this.force_manager = new ForceManager(
              this, new PVector(0, 0), new PVector(0, 0), FRICTION
      );
      this.damagable = new Damagable(this, HEALTH);
      this.attacker = new Attacker(this, new PVector(COLLISION_WIDTH / 2f, COLLISION_HEIGHT / -2f), PUNCH_RATE, PUNCH_FORCE, PUNCH_DAMAGE);
      this.controller = new AIMovementController(this, new PVector(COLLISION_WIDTH / 2f, -(1 - COLLISION_HEIGHT)), 10);

      // Add regular components
      this.components.add(new RectRenderer(this, COLOUR, COLLISION_WIDTH, COLLISION_HEIGHT));

      this.components.add(controller);
      this.components.add(force_manager);
      this.components.add(damagable);
      this.components.add(attacker);


      // Add collision components
      this.collision_components = new ArrayList<>();

      RectCollisionComponent core = new RectCollisionComponent(this, this::player_collision, COLLISION_WIDTH, COLLISION_HEIGHT);
      core.tag = BaseCollisionComponent.Tag.PRIMARY;

      this.collision_components.add(core);
      this.collision_components.add(new RectCollisionComponent(this, this::ground_collision, new PVector(0.05f, -(COLLISION_HEIGHT - 0f)), COLLISION_WIDTH - 0.1f, 0.05f));

      // Add collision components to regular
      this.components.addAll(this.collision_components);
   }



   // Methods
   @Override
   public boolean isDestroyed() {
      if(damagable.health <= 0)
         is_dead = true;
      return is_dead;
   }

   private boolean ground_collision(BaseCollisionComponent comp){
      if(comp.parent != sys.terrain)
         return false;

      force_manager.grounded = true;
      force_manager.set_grounded = true;

      return true;
   }


   private boolean player_collision(BaseCollisionComponent comp){
      if(!(comp.parent instanceof Player) || !ACTIVE)
         return false;

      controller.stop = true;
      attacker.punch((Player) comp.parent);

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

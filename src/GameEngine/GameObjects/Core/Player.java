package GameEngine.GameObjects.Core;

import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.Damagable;
import GameEngine.Components.ForceManager;
import GameEngine.Components.MovingSpriteManager;
import GameEngine.Components.PlayerComponents.*;
import GameEngine.Components.Renderers.CircleRenderer;
import GameEngine.Components.Renderers.ImageRenderer;
import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.Components.Weapons.MachineGun;
import GameEngine.Components.Weapons.RPG;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject implements Collideable {
   // Attributes
   public static boolean ACTIVE = true;

   public static final float COLLISION_HEIGHT= 0.75f;
   public static final float COLLISION_WIDTH = 0.5f;
   public static final float RENDER_WIDTH    = 0.6f;
   public static final float RENDER_HEIGHT   = 0.8f;
   public static final float ACCELERATION    = 12f;
   public static final float MAX_SPEED       = 4f;
   public static final float FRICTION        = 0.06f;
   public static final PVector COLOUR        = new PVector(0, 32, 128);
   public static final int HEALTH            = 100;

   public final ForceManager force_manager;
   private final ArrayList<BaseCollisionComponent> collision_components;
   private final Damagable damagable;
   private final CharacterController controller;

   public boolean is_dead = false;


   // Constructor
   public Player(GameEngine sys, PVector spawn_location){
      this(sys, spawn_location, true);
   }

   public Player(GameEngine sys, PVector spawn_location, boolean has_healthbar) {
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

      this.controller = new CharacterController(this, ACCELERATION, MAX_SPEED);

      this.damagable = new Damagable(this, COLLISION_WIDTH, COLLISION_HEIGHT, HEALTH);

      this.components.add(controller);
      this.components.add(new GrappleHook(this));
      this.components.add(force_manager);
      this.components.add(new PowerupManager(this));

      if(has_healthbar)
         this.components.add(new HealthBar(this, new PVector(20f, 20f), 75f, 10f));
      this.components.add(
         new MovingSpriteManager(this, new PVector(COLLISION_WIDTH / 2f, COLLISION_HEIGHT / -2f),
              "player_left", "player_right", "player_walking_left", "player_walking_right",
              RENDER_WIDTH, RENDER_HEIGHT, 0.8f, 0.90f,
              new PVector(0.27f, -0.30f)
      ));

      this.components.add(damagable);

      if(has_healthbar) {
         this.components.add(new GunController(this));
         this.components.add(RPG.create(this, new PVector(COLLISION_WIDTH / 2f, -COLLISION_HEIGHT / 2f)));
         this.components.add(MachineGun.create(this, new PVector(COLLISION_WIDTH / 2f, -COLLISION_HEIGHT / 2f)));
      }

      // Add collision components to regular
      this.components.addAll(this.collision_components);
   }

   // Methods
   @Override
   public boolean isDestroyed() {
      if(damagable.health <= 0)
         is_dead = true;
      if(is_dead)
         stop_audio();
      return is_dead;
   }


   public void stop_audio(){
      if(controller.running.isPlaying())
         controller.running.pause();
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

package GameEngine.GameObjects;

import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.PlayerComponents.*;
import GameEngine.Components.Renderers.CircleRenderer;
import GameEngine.Components.Renderers.ImageRenderer;
import GameEngine.GameEngine;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject implements Collideable {
   // Attributes
   public static final float COLLISION_RAD   = 0.25f;
   public static final float SPEED           = 2f;
   public static final String BODY_SPRITE    = "player_body";
   public static final int STARTING_LIVES    = 3;

   private final ArrayList<BaseCollisionComponent> collision_components;

   public boolean is_dead = false;


   // Constructor
   public Player(GameEngine sys, PVector spawn_location){
      this(sys, spawn_location, STARTING_LIVES);
   }

   public Player(GameEngine sys, PVector spawn_location, int number_lives) {
      super(sys);

      // Set position
      this.pos = spawn_location;

      // Add collision components
      this.collision_components = new ArrayList<>();
      this.collision_components.add(new CircleCollisionComponent(this, null, COLLISION_RAD));

      // Add regular components
      this.components.add(new ImageRenderer(this, BODY_SPRITE, COLLISION_RAD*2f, COLLISION_RAD*2f, new PVector(0, 0)));
      this.components.add(new CharacterController(this, SPEED));

      // Add collision components to regular
      this.components.addAll(this.collision_components);
   }

   // Methods
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

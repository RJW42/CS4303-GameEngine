package GameEngine.GameObjects;

import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.CircleCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.ForceManager;
import GameEngine.Components.PlayerComponents.*;
import GameEngine.Components.Renderers.CircleRenderer;
import GameEngine.Components.Renderers.ImageRenderer;
import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.GameEngine;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject implements Collideable {
   // Attributes
   public static final float COLLISION_HEIGHT= 0.75f;
   public static final float COLLISION_WIDTH = 0.5f;
   public static final float SPEED           = 0.1f;
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
      this.collision_components.add(new RectCollisionComponent(this, null, COLLISION_WIDTH, COLLISION_HEIGHT));
      //this.collision_components.add(new RectCollisionComponent(this, null, new PVector(0, -(COLLISION_HEIGHT - 0.05f)), COLLISION_WIDTH, 0.05f));

      // Add regular components
      this.components.add(new RectRenderer(this, new PVector(0, 32, 128), COLLISION_WIDTH, COLLISION_HEIGHT));
      this.components.add(new CharacterController(this, SPEED));
      this.components.add(new ForceManager(this, new PVector(0, 0), new PVector(0, 0)));

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

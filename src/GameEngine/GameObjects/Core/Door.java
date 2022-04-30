package GameEngine.GameObjects.Core;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.Components.TerrianComponents.DoorManager;
import GameEngine.Components.TerrianComponents.DoorRenderer;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Bullets.Bullet;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Door extends GameObject implements Collideable {
   // Attributes
   public static final String BASIC_OPEN_SPRITE    = "basic_door_opened";
   public static final String BASIC_CLOSE_SPRITE   = "basic_door_closed";
   public static final String KILL_OPEN_SPRITE     = "basic_door_opened";
   public static final String KILL_CLOSE_SPRITE    = "basic_door_closed";
   public static float TIME_TO_CLOSE   = 5f;
   public static float COLLISION_BONUS = 0.05f;

   public boolean is_dead = false;
   public boolean is_open = false;

   public final int lower_x;
   public final int lower_y;

   public RectCollisionComponent upper;
   public RectCollisionComponent middle;
   public RectCollisionComponent lower;

   private final ArrayList<BaseCollisionComponent> collision_components;
   public final DoorManager manager;

   // Constructor
   public Door(GameEngine sys, boolean shoot_to_open, int lower_x, int lower_y, RectCollisionComponent upper, RectCollisionComponent middle, RectCollisionComponent lower) {
      super(sys);

      // Init attributes
      this.pos = new PVector(lower_x, lower_y + Terrain.CELL_SIZE * 3);
      this.upper = upper;
      this.middle = middle;
      this.lower = lower;
      this.manager = new DoorManager(this);
      this.lower_x = lower_x;
      this.lower_y = lower_y;

      // Init collision components
      this.collision_components = new ArrayList<>();

      // Add regular components
      if(shoot_to_open) this.components.add(new DoorRenderer(this, BASIC_OPEN_SPRITE, BASIC_CLOSE_SPRITE));
      else this.components.add(new DoorRenderer(this, KILL_OPEN_SPRITE, KILL_CLOSE_SPRITE));
      this.components.add(manager);

      // Add collision components
      if(shoot_to_open)
         this.collision_components.add(
              new RectCollisionComponent(this, this::on_collision,new PVector(-COLLISION_BONUS, 0), Terrain.CELL_SIZE + COLLISION_BONUS * 2, Terrain.CELL_SIZE)
         );

      // Add collision components to regular
      this.components.addAll(this.collision_components);
   }


   // Methods
   private boolean on_collision(BaseCollisionComponent comp){
      if(comp.parent instanceof Bullet)
         manager.open(TIME_TO_CLOSE);
      return false;
   }

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

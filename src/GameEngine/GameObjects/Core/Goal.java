package GameEngine.GameObjects.Core;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.Renderers.GifRenderer;
import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import GameEngine.Levels.PlayLevel;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Goal extends GameObject implements Collideable {
   // Attributes
   public static final String GIF_SPRITE  = "goal";
   public static final int GIF_FPS        = 8;

   public boolean is_dead = false;

   private ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public Goal(GameEngine sys, PVector spawn_location) {
      super(sys);

      // Init attributes
      this.pos = spawn_location;

      // Add collision components 
      this.collision_components = new ArrayList<>();
      this.collision_components.add(new RectCollisionComponent(this, this::on_collision, new PVector(), Terrain.CELL_SIZE, Terrain.CELL_SIZE));

      // Add regular components
      this.components.add(new GifRenderer(this, GIF_SPRITE, GIF_FPS, Terrain.CELL_SIZE, Terrain.CELL_SIZE, new PVector(Terrain.CELL_SIZE / 2f, Terrain.CELL_SIZE / -2f), PConstants.PI * 2));


      // Add collision components to regular
      this.components.addAll(this.collision_components);
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   public boolean on_collision(BaseCollisionComponent other){
      if(!(other.parent instanceof Player))
         return false;

      // Player reached goal
      ((Player)other.parent).stop_audio();
      ((PlayLevel)sys.level_manager.getCurrentLevel()).player_reached_goal();
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

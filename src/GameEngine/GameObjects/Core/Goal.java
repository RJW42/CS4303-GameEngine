package GameEngine.GameObjects.Core;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Goal extends GameObject implements Collideable {
   // Attributes
   public static final PVector COLOUR = new PVector(0, 255, 0);

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
      this.components.add(new RectRenderer(this, COLOUR, Terrain.CELL_SIZE, Terrain.CELL_SIZE));


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
      System.out.println("Finished");
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

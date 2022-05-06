package GameEngine.GameObjects.Test;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class TestTerrian extends GameObject implements Collideable {
   // Attributes
   public boolean is_dead = false;

   private ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public TestTerrian(GameEngine sys, PVector pos) {
      super(sys);

      this.pos = pos;

      // Add collision components 
      this.collision_components = new ArrayList<>();
      this.collision_components.add(new RectCollisionComponent(this, null, 1f, 1f));

      // Add regular components
      this.components.add(new RectRenderer(this, new PVector(0, 127, 127), 1f, 1f));


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

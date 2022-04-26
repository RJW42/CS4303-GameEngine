package GameEngine.GameObjects.Bullets;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;

import java.util.ArrayList;
import java.util.List;


public class Explosion extends GameObject implements Collideable {
   // Attributes
   public boolean is_dead = false;

   private ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public Explosion(GameEngine sys) {
      super(sys);

      // Add collision components 
      this.collision_components = new ArrayList<>();
      // Todo: complete this 

      // Add regular components 
      // Todo: complete this 


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

package GameEngine.GameObjects;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.ForceManager;
import GameEngine.GameEngine;

import java.util.ArrayList;
import java.util.List;


public class Bullet extends GameObject implements Collideable {
   // Todo: implement

   // Attributes
   public boolean is_dead = false;

   private ArrayList<BaseCollisionComponent> collision_components;


   // Constructor
   public Bullet(GameEngine sys) {
      super(sys);

      // Add collision components 
      this.collision_components = new ArrayList<>();
      // Todo: complete this

      // Add regular components 
      // Todo: finish this this.components.add(new ForceManager(this, ));


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

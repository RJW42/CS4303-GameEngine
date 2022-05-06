package GameEngine.GameObjects.Test;


import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.ForceManager;
import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;


public class Pointer extends GameObject implements Collideable {
   // Attributes
   public boolean is_dead = false;

   private ArrayList<BaseCollisionComponent> collision_components;

   private InputManager.Key up;
   private InputManager.Key down;
   private InputManager.Key left;
   private InputManager.Key right;
   private ForceManager force_manager;

   // Constructor
   public Pointer(GameEngine sys) {
      super(sys);
      this.pos = new PVector(2, 2);
      this.up = sys.input_manager.getKey("mb-up");
      this.down = sys.input_manager.getKey("mb-down");
      this.left = sys.input_manager.getKey("mb-left");
      this.right = sys.input_manager.getKey("mb-right");

      // Add collision components 
      this.collision_components = new ArrayList<>();
      RectCollisionComponent core = new RectCollisionComponent(this, null, 0.5f, 0.75f);
      core.tag = BaseCollisionComponent.Tag.PRIMARY;
      core.bound_box_height *= 10f;
      core.bound_box_width *= 10;
      this.collision_components.add(core);

      // Add regular components 
      this.components.add(new RectRenderer(this, new PVector(255, 0, 0), 0.5f, 0.75f));
      this.force_manager = new ForceManager(this, new PVector(), new PVector(), 0f, false);
      this.components.add(force_manager);



      // Add collision components to regular
      this.components.addAll(this.collision_components);
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   @Override
   public void update() {
      if(up.pressed) force_manager.applyForce(new PVector(0, 20));
      if(down.pressed) force_manager.applyForce(new PVector(0, -20));
      if(left.pressed) force_manager.applyForce(new PVector(-20,0 ));
      if(right.pressed) force_manager.applyForce(new PVector(20, 0));

      super.update();
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

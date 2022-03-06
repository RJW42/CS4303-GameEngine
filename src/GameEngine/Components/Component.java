package GameEngine.Components;

import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

public abstract class Component {
   // Attributes
   public final GameEngine sys;
   public final GameObject parent;

   // Constructor
   public Component(GameObject parent) {
      this.parent = parent;
      this.sys = parent.sys;
   }

   // Methods
   public abstract void update();
   public abstract void draw();
}

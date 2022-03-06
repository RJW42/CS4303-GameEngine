package GameEngine.GameObjects;

import GameEngine.Components.Component;
import GameEngine.GameEngine;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {
   public final GameEngine sys;
   public PVector pos;
   protected ArrayList<Component> components;

   protected GameObject(GameEngine sys) {
      this.sys = sys;
      this.components = new ArrayList<>();
   }

   public void draw() {
      components.forEach(Component::draw);
   }

   public void update() {
      components.forEach(Component::update);
   }

   public <T extends Component> T getComponent(Class<T> type){
      for(var comp : components){
         if (type.isInstance(comp))
            return ((T) comp);
      }
      return null;
   }

   public <T extends Component> List<T> getComponents(Class<T> type){
      ArrayList<T> out = new ArrayList<>();
      for(var comp : components){
         if (type.isInstance(comp))
            out.add((T) comp);
      }
      return out;
   }


   public abstract boolean isDestroyed();
}

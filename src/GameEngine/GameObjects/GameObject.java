package GameEngine.GameObjects;

import GameEngine.Components.Component;
import GameEngine.Components.MapBuildingComponents.Tools.Tool;
import GameEngine.GameEngine;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObject {
   public final GameEngine sys;
   public PVector pos;
   protected ArrayList<Component> components;
   private ArrayList<Component> new_components;

   protected GameObject(GameEngine sys) {
      this.sys = sys;
      this.components = new ArrayList<>();
      this.new_components = new ArrayList<>();
   }

   public void start() {components.forEach(Component::start);}

   public void draw() {
      components.forEach(Component::draw);
   }

   public void update() {
      if(new_components.size() > 0) {
         components.addAll(new_components);
         new_components.clear();
      }

      components.forEach(Component::update);
   }

   public void onDeath(){

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

   public void addComponent(Component comp){
      this.new_components.add(comp);
   }


   public abstract boolean isDestroyed();
}

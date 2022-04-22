package GameEngine.Components.MapEditorComponents.Tools;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;


public class Exit extends Tool {
   // Attributes
   private Runnable on_exit;

   // Constructor
   public Exit(GameObject parent, Runnable on_exit) {
      super(parent);

      // init attributes
      this.icon_text = "Exit";
      this.on_exit = on_exit;
   }


   // Methods 
   public void start() {
   }

   public void update() {
      if(active){
         on_exit.run();
         active = false;
      }
   }

   public void draw() {
   }
}

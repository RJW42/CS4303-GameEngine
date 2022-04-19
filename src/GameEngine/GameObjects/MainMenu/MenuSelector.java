package GameEngine.GameObjects.MainMenu;


import GameEngine.Components.UIButton;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;


public class MenuSelector extends GameObject {
   // Attributes
   public boolean is_dead = false;
   // Todo: implement


   // Constructor
   public MenuSelector(GameEngine sys) {
      super(sys);

      // Add regular components 
      this.components.add(new UIButton(this, this::button_clicked, "Test",
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT /2),
              new PVector(255, 0, 0), new PVector(0, 255, 0), new PVector(0, 0, 255),
              new PVector(255, 127, 0), new PVector(127, 255, 0), new PVector(0, 127, 255),
              10, 2, 30, 30, true
      ));
   }


   // Methods
   public void button_clicked(){
   }

   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}

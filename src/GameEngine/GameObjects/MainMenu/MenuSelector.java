package GameEngine.GameObjects.MainMenu;


import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;


public class MenuSelector extends GameObject {
   // Attributes
   public boolean is_dead = false;
   // Todo: implement


   // Constructor
   public MenuSelector(GameEngine sys) {
      super(sys);

      // Add regular components 
      // Todo: complete this 
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}

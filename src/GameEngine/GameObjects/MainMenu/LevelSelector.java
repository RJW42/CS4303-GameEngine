package GameEngine.GameObjects.MainMenu;


import GameEngine.Components.UIButton;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;

import static GameEngine.GameObjects.MainMenu.MenuSelector.*;


public class LevelSelector extends GameObject {
   // Attributes
   public boolean is_dead = false;


   // Constructor
   public LevelSelector(GameEngine sys) {
      super(sys);

      // Add regular components 
      this.components.add(new UIButton(this, this::back_clicked, "Back",
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT /2),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      ));
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   public void back_clicked(){
      is_dead = true;
      sys.spawn(new MenuSelector(sys), 1);
   }
}

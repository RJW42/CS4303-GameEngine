package GameEngine.GameObjects.MainMenu;


import GameEngine.Components.UIComponents.UIButton;
import GameEngine.Components.UIComponents.UIInput;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;

import static GameEngine.GameObjects.MainMenu.MenuSelector.*;
import static GameEngine.GameObjects.MainMenu.MenuSelector.HEIGHT;


public class MapEditorSelector extends GameObject {
   // Attributes
   public static final int MAX_NAME_LENGTH = 10;

   public boolean is_dead = false;


   // Constructor
   public MapEditorSelector(GameEngine sys) {
      super(sys);

      // Create buttons
      UIButton back_button = new UIButton(this, this::back_clicked, "Back",
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT / 2 + 400),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      UIInput name_input = new UIInput(this, this::name_provided, "Name: ",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, PADDING, BORDER_WIDTH,
              WIDTH * 2f, HEIGHT, MAX_NAME_LENGTH
      );

      // Add components
      this.components.add(back_button);
      this.components.add(name_input);
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   public void back_clicked(){
      // Switch back to main menu
      is_dead = true;
      sys.spawn(new MenuSelector(sys), 1);
   }

   public void name_provided(String name){
      System.out.println(name);
   }
}

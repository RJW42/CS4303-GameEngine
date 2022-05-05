package GameEngine.GameObjects.MainMenu;


import GameEngine.Components.UIComponents.ControlsList;
import GameEngine.Components.UIComponents.UIButton;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;

import static GameEngine.GameObjects.MainMenu.MenuSelector.*;
import static GameEngine.GameObjects.MainMenu.MenuSelector.HEIGHT;


public class ControlsMenu extends GameObject {
   // Attributes
   public boolean is_dead = false;
   private ControlsList controls_list;


   // Constructor
   public ControlsMenu(GameEngine sys) {
      super(sys);

      // Add regular components
      UIButton back_button = new UIButton(this, this::back_clicked, "Back",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f + 400),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      UIButton reset_button = new UIButton(this, this::reset_clicked, "Reset",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f + 400),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      reset_button.pos.x += reset_button.width / 2f + 5;
      back_button.pos.x -= back_button.width / 2f + 5;

      controls_list = new ControlsList(this, new PVector(
              GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f)
      );


      // Add components
      this.components.add(controls_list);
      this.components.add(back_button);
      this.components.add(reset_button);
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   public void back_clicked(){
      // Switch back to menu selector
      is_dead = true;
      sys.spawn(new MenuSelector(sys), 3);
   }

   public void reset_clicked(){
      sys.input_manager.reset_and_reload();
      controls_list.refresh();
   }
}

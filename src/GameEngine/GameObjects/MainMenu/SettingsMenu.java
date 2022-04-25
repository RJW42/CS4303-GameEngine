package GameEngine.GameObjects.MainMenu;


import GameEngine.Components.UIComponents.UIButton;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;

import static GameEngine.GameObjects.MainMenu.MenuSelector.*;
import static GameEngine.GameObjects.MainMenu.MenuSelector.HEIGHT;


public class SettingsMenu extends GameObject {
   // Attributes
   public boolean is_dead = false;


   // Constructor
   public SettingsMenu(GameEngine sys) {
      super(sys);

      // Add regular components 
      UIButton back_button = new UIButton(this, this::back_clicked, "Back",
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT / 2 + 200),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      UIButton apply_button = new UIButton(this, this::apply_clicked, "Apply",
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT / 2),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      UIButton reset_button = new UIButton(this, this::reset_clicked, "Reset",
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT / 2 - 200),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      // Add components
      this.components.add(back_button);
      this.components.add(apply_button);
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
      sys.spawn(new MenuSelector(sys), 1);
   }

   public void apply_clicked(){
      sys.config.save(GameEngine.CONFIG_FOLDER + GameEngine.CONFIG_FILE);
      sys.restart();
   }

   public void reset_clicked(){
      //sys.config.reset_to_default();
      sys.restart();
   }
}

package GameEngine.GameObjects.MainMenu;


import GameEngine.Components.UIComponents.ControlsList;
import GameEngine.Components.UIComponents.LevelList;
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
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH - 2.5f / GameEngine.UI_SCALE, HEIGHT, true
      );

      back_button.rect_alpha_colour = BUTTON_ALPHA;
      back_button.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;

      UIButton reset_button = new UIButton(this, this::reset_clicked, "Reset",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH - 2.5f / GameEngine.UI_SCALE, HEIGHT, true
      );

      reset_button.rect_alpha_colour = BUTTON_ALPHA;
      reset_button.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;

      reset_button.pos.x += reset_button.width / 2f + 2.5f;
      back_button.pos.x -= back_button.width / 2f + 2.5f;

      controls_list = new ControlsList(this, new PVector(
              GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f + ((ControlsList.MAX_ITEMS_ON_LIST + 1) * HEIGHT * GameEngine.UI_SCALE) / 2f -
              (5f + HEIGHT * GameEngine.UI_SCALE) / 2f), BUTTON_ALPHA, BUTTON_HOVER_ALPHA
      );

      reset_button.pos.y = controls_list.pos.y + controls_list.height / 2f + 5f; // + HEIGHT * GameEngine.UI_SCALE;
      back_button.pos.y = controls_list.pos.y + controls_list.height / 2f + 5f; // + HEIGHT * GameEngine.UI_SCALE;

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

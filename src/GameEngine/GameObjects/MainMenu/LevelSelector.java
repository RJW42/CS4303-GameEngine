package GameEngine.GameObjects.MainMenu;


import GameEngine.Components.UIComponents.LevelList;
import GameEngine.Components.UIComponents.UIButton;
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

      // Create buttons
      UIButton back_button = new UIButton(this, this::back_clicked, "Back",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f + 400),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH * 2, HEIGHT, true
      );

      back_button.rect_alpha_colour = BUTTON_ALPHA;
      back_button.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;

      LevelList levels_list = new LevelList(this,
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f + ((LevelList.MAX_NUM_ROWS + 1) * HEIGHT * GameEngine.UI_SCALE) / 2f -
                      (SPACING * GameEngine.UI_SCALE + HEIGHT * GameEngine.UI_SCALE) / 2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, BUTTON_ALPHA, BUTTON_HOVER_ALPHA
      );

      // Set spacing
      back_button.pos.y = levels_list.pos.y + levels_list.height / 2f + SPACING * GameEngine.UI_SCALE;

      // Add components
      this.components.add(back_button);
      this.components.add(levels_list);
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
}

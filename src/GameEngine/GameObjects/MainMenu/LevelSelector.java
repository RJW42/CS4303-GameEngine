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
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT / 2 + 400),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      LevelList levels_list = new LevelList(this,
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT / 2),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT
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
      is_dead = true;
      sys.spawn(new MenuSelector(sys), 1);
   }
}

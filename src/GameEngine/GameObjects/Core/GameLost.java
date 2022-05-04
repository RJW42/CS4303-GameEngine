package GameEngine.GameObjects.Core;


import GameEngine.Components.UIComponents.UIButton;
import GameEngine.Components.UIComponents.UITextRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import GameEngine.Levels.PlayLevel;
import processing.core.PConstants;
import processing.core.PVector;

import static GameEngine.GameObjects.MainMenu.MenuSelector.*;


public class GameLost extends GameObject {
   // Attributes
   public static final PVector MESSAGE_TEXT_COLOUR = new PVector(255, 0, 0);

   public boolean is_dead = false;
   public Player player;


   // Constructor
   public GameLost(GameEngine sys, Player player) {
      super(sys);

      // Init attributes
      this.player = player;

      // Add regular components
      UITextRenderer renderer = new UITextRenderer(this,
              new PVector(GameEngine.SCREEN_WIDTH /2f, GameEngine.SCREEN_HEIGHT / 2f),
              MESSAGE_TEXT_COLOUR, "Game Over!", PConstants.CENTER, 100, 20
      );

      UIButton restart_button = new UIButton(this,
         this::restart, "Restart", new PVector(), TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR,
         TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR, 5f, 1f, 50f, 20f, true
      );

      UIButton menu_button = new UIButton(this,
              this::menu, "Menu", new PVector(), TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR,
              TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR, 5f, 1f, 50f, 20f, true
      );

      restart_button.pos.y = renderer.text_pos.y - renderer.max_height;
      menu_button.pos.y = renderer.text_pos.y - renderer.max_height;

      restart_button.pos.x = renderer.text_pos.x - (restart_button.width /2f + 5f);
      menu_button.pos.x = renderer.text_pos.x + menu_button.width /2f + 5f;

      this.components.add(renderer);
      this.components.add(restart_button);
      this.components.add(menu_button);
   }


   // Methods
   public void restart(){
      ((PlayLevel)sys.level_manager.getCurrentLevel()).restart();
   }

   public void menu(){
      ((PlayLevel)sys.level_manager.getCurrentLevel()).menu();
   }

   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}

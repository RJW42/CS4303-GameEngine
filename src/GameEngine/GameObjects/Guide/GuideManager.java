package GameEngine.GameObjects.Guide;


import GameEngine.Components.Renderers.RectRenderer;
import GameEngine.Components.UIComponents.UIButton;
import GameEngine.Components.UIComponents.UIInput;
import GameEngine.Components.UIComponents.UIRectRenderer;
import GameEngine.GameObjects.Core.Goal;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import GameEngine.GameObjects.MainMenu.MenuSelector;
import processing.core.PVector;

import static GameEngine.GameObjects.MainMenu.MenuSelector.*;

public class GuideManager extends GameObject {
   // Attributes
   public boolean is_dead = false;

   public float box_width;
   public float box_height;
   public UIButton prev;
   public UIButton next;
   public UIButton menu;


   // Constructor
   public GuideManager(GameEngine sys) {
      super(sys);

      // Add regular components 
      this.init_buttons();

      this.components.add(prev);
      this.components.add(next);
      this.components.add(menu);
   }


   // Methods
   public void prev_clicked(){

   }


   public void next_clicked(){

   }


   public void menu_clicked(){
      // Go back to menu
      is_dead = true;
      sys.spawn(new MenuSelector(sys), 3);
   }


   @Override
   public boolean isDestroyed() {
      return is_dead;
   }


   private void init_buttons(){
      // Create three buttons
      menu = new UIButton(this, this::menu_clicked, "Back",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT /2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      menu.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;
      menu.rect_alpha_colour = BUTTON_ALPHA;

      prev = new UIButton(this, this::prev_clicked, "Previous",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT /2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      prev.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;
      prev.rect_alpha_colour = BUTTON_ALPHA;
      prev.text_colour = new PVector(127, 127, 127);
      prev.hover_text_colour = new PVector(127, 127, 127);

      next = new UIButton(this, this::next_clicked, "Next",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT /2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      next.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;
      next.rect_alpha_colour = BUTTON_ALPHA;

      prev.pos.x -= prev.width + SPACING * GameEngine.UI_SCALE;
      next.pos.x += next.width + SPACING * GameEngine.UI_SCALE;

      box_width = prev.width * 3 + SPACING * GameEngine.UI_SCALE * 2;
      box_height = box_width * 0.70f;

      prev.pos.y -= box_height / 2f + prev.height / 4f;
      next.pos.y -= box_height / 2f + prev.height / 4f;
      menu.pos.y -= box_height / 2f + prev.height / 4f;

      UIRectRenderer rect = new UIRectRenderer(this,
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f), BUTTON_COLOUR, BORDER_COLOUR, BORDER_WIDTH,
              box_width / GameEngine.UI_SCALE, box_height / GameEngine.UI_SCALE, true
      );

      rect.pos.y += menu.height / 4f + SPACING * GameEngine.UI_SCALE;
      rect.alpha = BUTTON_ALPHA;

      this.components.add(rect);
   }
}

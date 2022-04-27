package GameEngine.GameObjects;


import GameEngine.Components.UIComponents.UITextRenderer;
import GameEngine.Components.WarningTimer;
import GameEngine.GameEngine;
import processing.core.PConstants;
import processing.core.PVector;


public class WarningDisplay extends GameObject {
   // Attributes
   public static final PVector COLOUR = new PVector(255, 0, 0);

   public boolean is_dead = false;

   private final UITextRenderer text_renderer;
   private final WarningTimer warning_timer;

   // Constructor
   public WarningDisplay(GameEngine sys) {
      super(sys);

      // Create renderer
      int max_height = 20;
      int max_width = (int)((GameEngine.SCREEN_WIDTH - 100f) / GameEngine.UI_SCALE);

      PVector pos = new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT - max_height * GameEngine.UI_SCALE - 50);

      text_renderer = new UITextRenderer(
              this, pos, COLOUR, "", PConstants.CENTER, max_width, max_height
      );

      warning_timer = new WarningTimer(this);

      // Add renderer to components
      this.components.add(text_renderer);
      this.components.add(warning_timer);
   }

   public void display_warning(String warning){
      warning_timer.display_warning(warning);
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }
}

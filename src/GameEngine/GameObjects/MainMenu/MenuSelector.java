package GameEngine.GameObjects.MainMenu;


import GameEngine.Components.UIButton;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;


public class MenuSelector extends GameObject {
   // Attributes
   public static final PVector BUTTON_HOVER_COLOUR = new PVector(255, 207, 64);
   public static final PVector BORDER_HOVER_COLOUR = new PVector(255, 255, 224);
   public static final PVector TEXT_HOVER_COLOUR   = new PVector();
   public static final PVector BUTTON_COLOUR       = new PVector(168, 168, 168);
   public static final PVector BORDER_COLOUR       = new PVector(66, 66, 66);
   public static final PVector TEXT_COLOUR         = new PVector(224, 244, 255);

   public static final float BORDER_WIDTH = 2;
   public static final float PADDING      = 10;
   public static final float WIDTH        = 100;
   public static final float HEIGHT       = 20;
   public static final float SPACING      = 10;

   public boolean is_dead = false;

   private UIButton level_select;
   private UIButton editor_select;
   private UIButton controls;


   // Constructor
   public MenuSelector(GameEngine sys) {
      super(sys);

      // Create Buttons
      init_buttons();

      this.components.add(level_select);
      this.components.add(editor_select);
      this.components.add(controls);
   }


   // Methods
   public void levels_clicked(){
      // Switch to level selector
      is_dead = true;
      sys.spawn(new LevelSelector(sys), 1);
   }

   public void editor_clicked(){

   }

   public void controls_clicked(){

   }

   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   private void init_buttons(){
      // Create buttons
      level_select = new UIButton(this, this::levels_clicked, "Levels",
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT /2),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      editor_select =  new UIButton(this, this::editor_clicked, "Map Editor",
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT /2),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      controls = new UIButton(this, this::controls_clicked, "Controls",
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT /2),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      // Update buttons positions based off of button height
      level_select.pos.y += level_select.height + SPACING * GameEngine.UI_SCALE;
      controls.pos.y -= controls.height + SPACING * GameEngine.UI_SCALE;

   }
}

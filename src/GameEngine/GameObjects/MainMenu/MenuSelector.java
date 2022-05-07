package GameEngine.GameObjects.MainMenu;


import GameEngine.Components.UIComponents.UIButton;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PVector;


public class MenuSelector extends GameObject {
   // Attributes
   public static final PVector BUTTON_HOVER_COLOUR = new PVector(66, 245, 245);
   public static final PVector BORDER_HOVER_COLOUR = new PVector(255, 255, 224);
   public static final PVector TEXT_HOVER_COLOUR   = new PVector(224, 244, 255);
   public static final PVector BUTTON_COLOUR       = new PVector(0, 0, 0);
   public static final PVector BORDER_COLOUR       = new PVector(0, 0, 0);
   public static final PVector TEXT_COLOUR         = new PVector(224, 244, 255);

   public static final float BUTTON_HOVER_ALPHA = 145;
   public static final float BUTTON_ALPHA = 145;
   public static final float BORDER_WIDTH = 2;
   public static final float PADDING      = 10;
   public static final float WIDTH        = 100;
   public static final float HEIGHT       = 20;
   public static final float SPACING      = 10;

   public boolean is_dead = false;

   private UIButton level_select;
   private UIButton editor_select;
   private UIButton controls;
   private UIButton settings;


   // Constructor
   public MenuSelector(GameEngine sys) {
      super(sys);

      // Create Buttons
      init_buttons();

      this.components.add(level_select);
      this.components.add(editor_select);
      this.components.add(controls);
      this.components.add(settings);
   }


   // Methods
   public void levels_clicked(){
      // Switch to level selector
      is_dead = true;
      sys.spawn(new LevelSelector(sys), 3);
   }

   public void editor_clicked(){
      // Switch to editor selector
      is_dead = true;
      sys.spawn(new MapEditorSelector(sys), 3);
   }

   public void controls_clicked(){
      // Switch to controls menu
      is_dead = true;
      sys.spawn(new ControlsMenu(sys), 3);
   }

   public void settings_clicked(){
      // Switch to settings menu
      is_dead = true;
      sys.spawn(new SettingsMenu(sys), 3);
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

      level_select.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;
      level_select.rect_alpha_colour = BUTTON_ALPHA;

      editor_select =  new UIButton(this, this::editor_clicked, "Map Editor",
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT /2),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      editor_select.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;
      editor_select.rect_alpha_colour = BUTTON_ALPHA;

      controls = new UIButton(this, this::controls_clicked, "Controls",
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT /2),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      controls.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;
      controls.rect_alpha_colour = BUTTON_ALPHA;

      settings = new UIButton(this, this::settings_clicked, "Settings",
              new PVector(GameEngine.SCREEN_WIDTH / 2, GameEngine.SCREEN_HEIGHT /2),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH, HEIGHT, true
      );

      settings.hover_rect_alpha_colour = BUTTON_HOVER_ALPHA;
      settings.rect_alpha_colour = BUTTON_ALPHA;

      // Update buttons positions based off of button height
      float total_height = level_select.height * 4 + SPACING * 3 * GameEngine.UI_SCALE;

      level_select.pos.y += total_height / 2;
      editor_select.pos.y = level_select.pos.y - (level_select.height + SPACING * GameEngine.UI_SCALE);
      controls.pos.y = editor_select.pos.y  - (editor_select.height + SPACING * GameEngine.UI_SCALE);
      settings.pos.y = controls.pos.y - (controls.height + SPACING * GameEngine.UI_SCALE);
   }
}

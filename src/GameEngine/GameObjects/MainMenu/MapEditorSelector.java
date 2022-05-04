package GameEngine.GameObjects.MainMenu;


import GameEngine.Components.UIComponents.UIButton;
import GameEngine.Components.UIComponents.UIInput;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import GameEngine.Levels.MainMenu;
import GameEngine.Levels.MapBuilder;
import processing.core.PVector;

import java.util.UUID;

import static GameEngine.GameObjects.MainMenu.MenuSelector.*;
import static GameEngine.GameObjects.MainMenu.MenuSelector.HEIGHT;


public class MapEditorSelector extends GameObject {
   // Attributes
   public static final int MAX_NAME_LENGTH = 10;
   public static final int MAX_SIZE_LENGTH = 3;
   public static final float INPUT_SPACING = 1f;
   public static final PVector ERR_COLOUR  = new PVector(255, 0, 0);

   public boolean is_dead = false;

   private final UIInput name_input;
   private final UIInput width_input;
   private final UIInput height_input;
   private final UIButton create_button;

   private int width, height;
   private String name;


   // Constructor
   public MapEditorSelector(GameEngine sys) {
      super(sys);

      // Init attributes
      width = 0;
      height = 0;
      name = null;

      // Create buttons
      UIButton back_button = new UIButton(this, this::back_clicked, "Back",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH - INPUT_SPACING, HEIGHT, true
      );

      create_button = new UIButton(this, this::create_clicked, "Create",
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, WIDTH - INPUT_SPACING, HEIGHT, true
      );

      name_input = new UIInput(this, this::name_provided, "Name: ", null,
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, PADDING, BORDER_WIDTH,
              WIDTH * 2f, HEIGHT, MAX_NAME_LENGTH
      );

      width_input =  new UIInput(this, this::width_provided, "Width: ", null,
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, PADDING, BORDER_WIDTH,
              WIDTH - INPUT_SPACING, HEIGHT, MAX_SIZE_LENGTH
      );

      height_input =  new UIInput(this, this::height_provided, "Height: ", null,
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, PADDING, BORDER_WIDTH,
              WIDTH - INPUT_SPACING, HEIGHT, MAX_SIZE_LENGTH
      );

      // Set button positions
      name_input.pos.y = width_input.pos.y + width_input.height + INPUT_SPACING * GameEngine.UI_SCALE;

      width_input.pos.x -= width_input.width / 2f + INPUT_SPACING * GameEngine.UI_SCALE;
      height_input.pos.x += height_input.width / 2f + INPUT_SPACING * GameEngine.UI_SCALE;

      back_button.pos.x = width_input.pos.x;
      back_button.pos.y = width_input.pos.y - (width_input.height + INPUT_SPACING * GameEngine.UI_SCALE);

      create_button.pos.y = back_button.pos.y;
      create_button.pos.x = height_input.pos.x;


      // Add components
      this.components.add(back_button);
      this.components.add(create_button);
      this.components.add(name_input);
      this.components.add(width_input);
      this.components.add(height_input);
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   public void back_clicked(){
      // Switch back to main menu
      sys.input_manager.cancelInput();
      is_dead = true;
      sys.spawn(new MenuSelector(sys), 3);
   }

   public void create_clicked(){
      // Check can create level
      if(name == null || width <= 0 || height <= 0 ||
         name_input.reading_input || width_input.reading_input ||
         height_input.reading_input
      ){
         create_button.border_colour = ERR_COLOUR;
         create_button.hover_border_colour = ERR_COLOUR;
         sys.warning_display.display_warning("Enter all required values");
         return;
      }

      // All valid enter level editor
      ((MainMenu)sys.level_manager.getCurrentLevel()).advance = (new MapBuilder(sys, width, height, name));
   }

   public void name_provided(String name){
      // Check if name is valid
      if(name.length() < 1){
         // Invalid
         name_input.border_colour = ERR_COLOUR;
         sys.warning_display.display_warning("Level name bust be greater than 1");
         this.name = null;
         return;
      }

      name_input.border_colour = BORDER_COLOUR;
      this.name = name;
   }

   public void width_provided(String width){
      // Check if width is valid
      int w = parse_int(width);

      if(w <= 10){
         width_input.border_colour = ERR_COLOUR;
         sys.warning_display.display_warning("Width must be a number greater than 10");
         this.width = 0;
         return;
      }

      width_input.border_colour = BORDER_COLOUR;
      this.width = w;
   }

   public void height_provided(String height){
      // Check if width is valid
      int h = parse_int(height);

      if(h <= 10){
         height_input.border_colour = ERR_COLOUR;
         sys.warning_display.display_warning("Height must be a number greather than 10");
         this.height = 0;
         return;
      }

      height_input.border_colour = BORDER_COLOUR;
      this.height = h;
   }

   private int parse_int(String s){
      try{
         return Integer.parseInt(s);
      } catch(Exception e){
         return 0;
      }
   }
}

package GameEngine.GameObjects.MainMenu;


import GameEngine.Components.UIComponents.UIButton;
import GameEngine.Components.UIComponents.UIInput;
import GameEngine.Components.UIComponents.UIRectRenderer;
import GameEngine.Components.UIComponents.UITextRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameEngine;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.ArrayList;

import static GameEngine.GameObjects.MainMenu.MenuSelector.*;
import static GameEngine.GameObjects.MainMenu.MenuSelector.HEIGHT;


public class SettingsMenu extends GameObject {
   // Attributes
   public static final PVector ERR_COLOUR = new PVector(255, 0, 0);
   public static final float TOTAL_WIDTH  = WIDTH * 2;
   public static final float INTERNAL_PAD = 5;
   public static final float SPACING      = 5;
   public static final float NAME_RATIO   = 0.5f;
   public static final float ELEMENT_PAD  = 2;

   public boolean is_dead = false;

   private boolean full_screen;
   private int screen_width;
   private int screen_height;
   private int ui_scale;
   private int display;

   private UIButton back_button;
   private UIButton apply_button;
   private UIButton reset_button;
   private UIButton fullscreen_toggle_button;

   private UIInput display_input;
   private UIInput ui_scale_input;
   private UIInput screen_width_input;
   private UIInput screen_height_input;


   private float accent;
   private float decent;

   // Constructor
   public SettingsMenu(GameEngine sys) {
      super(sys);

      // Init attributes
      full_screen = sys.config.full_screen;
      screen_width = sys.config.screen_width;
      screen_height = sys.config.screen_height;
      ui_scale = sys.config.ui_scale;
      display = sys.config.display;


      // Create menu buttons
      init_buttons();
      init_elements();

      // Add components
      this.components.add(back_button);
      this.components.add(apply_button);
      this.components.add(reset_button);
      this.components.add(fullscreen_toggle_button);
      this.components.add(display_input);
      this.components.add(ui_scale_input);
      this.components.add(screen_width_input);
      this.components.add(screen_height_input);
   }


   // Methods
   @Override
   public boolean isDestroyed() {
      return is_dead;
   }

   private void back_clicked(){
      // Switch back to menu selector
      is_dead = true;
      sys.spawn(new MenuSelector(sys), 1);
   }

   private void apply_clicked(){
      sys.config.full_screen = full_screen;
      sys.config.screen_height = screen_height;
      sys.config.screen_width = screen_width;
      sys.config.display = display;
      sys.config.ui_scale = ui_scale;
      sys.config.save(GameEngine.CONFIG_FOLDER + GameEngine.CONFIG_FILE);
      sys.restart();
   }

   private void reset_clicked(){
      sys.config.reset_to_default();
      sys.restart();
   }

   private void toggle_full_screen(){
      full_screen = !full_screen;
      fullscreen_toggle_button.text = get_full_screen_text();
   }

   private void display_callback(String value){
      int d = parse_int(value);

      if(d < 0){
         display_input.border_colour = ERR_COLOUR;
         sys.warning_display.display_warning("Display must be a non negative int");
         return;
      }

      display_input.border_colour = BORDER_COLOUR;
      display = d;
   }

   private void display_width_callback(String value){
      int w = parse_int(value);

      if(w < 250){
         screen_width_input.border_colour = ERR_COLOUR;
         sys.warning_display.display_warning("Width must be greater than 250");
         return;
      }

      screen_width_input.border_colour = BORDER_COLOUR;
      screen_width = w;
   }

   private void display_height_callback(String value){
      int h = parse_int(value);

      if(h < 250){
         screen_height_input.border_colour = ERR_COLOUR;
         sys.warning_display.display_warning("Height must be greater than 250");
         return;
      }

      screen_height_input.border_colour = BORDER_COLOUR;
      screen_height = h;
   }

   private void ui_scale_callback(String value){
      int s = parse_int(value);

      if(s <= 1){
         ui_scale_input.border_colour = ERR_COLOUR;
         sys.warning_display.display_warning("Scale must be greater than 0");
         return;
      }

      ui_scale_input.border_colour = BORDER_COLOUR;
      ui_scale = s;
   }


   private void init_buttons() {
      // Create all button components
      float button_x = GameEngine.SCREEN_WIDTH / 2f;
      float button_y = GameEngine.SCREEN_HEIGHT / 2f + 400;

      back_button = new UIButton(this, this::back_clicked, "Back", new PVector(button_x, button_y),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, TOTAL_WIDTH / 3, HEIGHT, true
      );

      apply_button = new UIButton(this, this::apply_clicked, "Apply", new PVector(button_x, button_y),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, TOTAL_WIDTH / 3, HEIGHT, true
      );

      reset_button = new UIButton(this, this::reset_clicked, "Reset", new PVector(button_x, button_y),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              PADDING, BORDER_WIDTH, TOTAL_WIDTH / 3, HEIGHT, true
      );

      back_button.pos.x -= back_button.width + SPACING;
      reset_button.pos.x += reset_button.width + SPACING;
   }


   private void init_elements(){
      // Create elements background
      UIRectRenderer elements_background = new UIRectRenderer(this,
              new PVector(GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f),
              BUTTON_COLOUR, BORDER_COLOUR, BORDER_WIDTH, TOTAL_WIDTH, HEIGHT, true
      );

      elements_background.width += SPACING * 2;
      elements_background.height = back_button.height * 5 + SPACING * 6;
      elements_background.pos.y += back_button.height / 2f + SPACING / 2f;

      float button_y = elements_background.pos.y - (elements_background.height + back_button.height) / 2f - SPACING;

      back_button.pos.y = button_y;
      reset_button.pos.y = button_y;
      apply_button.pos.y = button_y;


      // Create settings elements
      ArrayList<UITextRenderer> renderers = init_renderers(elements_background);

      // Create buttons for each element
      create_full_screen_buttons(renderers.get(0), elements_background);
      display_input = get_input(renderers.get(1), this::display_callback, "" + display, elements_background);
      ui_scale_input = get_input(renderers.get(2), this::ui_scale_callback, "" + ui_scale, elements_background);
      screen_width_input = get_input(renderers.get(3), this::display_width_callback, "" + screen_width, elements_background);
      screen_height_input = get_input(renderers.get(4), this::display_height_callback, "" + screen_height, elements_background);

      this.components.add(elements_background);
      this.components.addAll(renderers);
   }


   private ArrayList<UITextRenderer> init_renderers(UIRectRenderer elements_background){
      ArrayList<UITextRenderer> renderers = new ArrayList<>();

      renderers.add(new UITextRenderer(this, new PVector(),
              TEXT_COLOUR, "Fullscreen: ", PConstants.LEFT, (int)(TOTAL_WIDTH * NAME_RATIO), (int)HEIGHT
      ));


      renderers.add(new UITextRenderer(this, new PVector(),
              TEXT_COLOUR, "Display: ", PConstants.LEFT, (int)(TOTAL_WIDTH * NAME_RATIO), (int)HEIGHT
      ));

      renderers.add(new UITextRenderer(this, new PVector(),
              TEXT_COLOUR, "UI scale: ", PConstants.LEFT, (int)(TOTAL_WIDTH * NAME_RATIO), (int)HEIGHT
      ));

      renderers.add(new UITextRenderer(this, new PVector(),
              TEXT_COLOUR, "Display width: ", PConstants.LEFT, (int)(TOTAL_WIDTH * NAME_RATIO), (int)HEIGHT
      ));

      renderers.add(new UITextRenderer(this, new PVector(),
              TEXT_COLOUR, "Display height: ", PConstants.LEFT, (int)(TOTAL_WIDTH * NAME_RATIO), (int)HEIGHT
      ));


      float height = HEIGHT * GameEngine.UI_SCALE;
      int min_max_height = renderers.stream().map(r -> r.max_height).min(Integer::compareTo).get();
      int min_text_size = renderers.stream().map(r -> r.text_size).min(Integer::compareTo).get();
      sys.textSize(min_text_size);

      accent = sys.textAscent();
      decent = sys.textDescent();

      renderers.forEach(r -> {
         r.text_size = min_text_size;
         r.max_height = min_max_height;
      });

      renderers.get(0).text_pos.x = elements_background.pos.x - elements_background.width / 2f + SPACING;
      renderers.get(0).text_pos.y = elements_background.pos.y + elements_background.height / 2f - accent - SPACING;

      UITextRenderer prev = renderers.get(0);
      for(int i = 1; i < renderers.size(); i++){
         UITextRenderer renderer = renderers.get(i);
         renderer.text_pos.x = prev.text_pos.x;
         renderer.text_pos.y = (prev.text_pos.y - height) - SPACING;
         prev = renderer;
      }

      return renderers;
   }


   private void create_full_screen_buttons(UITextRenderer renderer, UIRectRenderer elements_background){
      // Create button
      fullscreen_toggle_button = new UIButton(this, this::toggle_full_screen,  get_full_screen_text(), new PVector(),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, TEXT_HOVER_COLOUR, BUTTON_HOVER_COLOUR, BORDER_HOVER_COLOUR,
              ELEMENT_PAD, BORDER_WIDTH, TOTAL_WIDTH * (1 - NAME_RATIO) - INTERNAL_PAD, HEIGHT - INTERNAL_PAD, true
      );

      fullscreen_toggle_button.pos.x = renderer.text_pos.x + elements_background.width * (1 - NAME_RATIO) + fullscreen_toggle_button.width / 2f + (INTERNAL_PAD * GameEngine.UI_SCALE) / 2f;
      fullscreen_toggle_button.pos.y = renderer.text_pos.y + fullscreen_toggle_button.height / 2f;
   }


   private UIInput get_input(UITextRenderer renderer, UIInput.CallBack callBack, String placeholder, UIRectRenderer elements_background) {
      // Create input
      UIInput input = new UIInput(this, callBack, "", placeholder, new PVector(),
              TEXT_COLOUR, BUTTON_COLOUR, BORDER_COLOUR, ELEMENT_PAD, BORDER_WIDTH, TOTAL_WIDTH * (1 - NAME_RATIO) - INTERNAL_PAD,
              HEIGHT - INTERNAL_PAD, 4
      );

      input.pos.x = renderer.text_pos.x + elements_background.width * (1 - NAME_RATIO) + input.width / 2f + (INTERNAL_PAD * GameEngine.UI_SCALE) / 2f;
      input.pos.y = renderer.text_pos.y + input.height / 2f;

      return input;
   }

   private String get_full_screen_text(){
      return (full_screen) ? "Disable" : "Enable";
   }


   private int parse_int(String s){
      try{
         return Integer.parseInt(s);
      } catch(Exception e){
         return -1;
      }
   }
}

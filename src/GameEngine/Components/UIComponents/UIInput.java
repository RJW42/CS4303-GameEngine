package GameEngine.Components.UIComponents;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PConstants;
import processing.core.PVector;


public class UIInput extends Component {
   // Attributes
   public PVector text_colour;
   public PVector rect_colour;
   public PVector border_colour;
   public PVector pos;
   public float width;
   public float height;
   public String prompt;
   public int max_input_length;

   private String input;

   private float padding;
   private float border_width;
   private float text_height;
   private int text_size;
   private CallBack callback;

   private boolean mouse_over;
   private boolean was_clicked;
   private InputManager.Key mouse_click;
   public boolean reading_input;

   private float time_since_blink;
   private float blink_time = 0.4f;
   private boolean show_underscore = false;

   // Constructor
   public UIInput(GameObject parent, CallBack callback, String prompt, PVector pos, PVector text_colour, PVector rect_colour, PVector border_colour, float padding, float border_width, float width, float height, int max_input_length) {
      super(parent);

      // Init Attributes
      this.pos = pos;
      this.width = width;
      this.height = height;
      this.prompt = prompt;
      this.padding = padding;
      this.border_width = border_width;
      this.text_colour = text_colour;
      this.rect_colour = rect_colour;
      this.border_colour = border_colour;
      this.callback = callback;
      this.input = "";
      this.max_input_length = max_input_length;

      this.mouse_click = sys.input_manager.mouse_click;
      this.was_clicked = mouse_click.pressed;

      // Update sizes
      apply_scaling();
      reset_text_size();
   }


   // Methods 
   public void start() {
      // Todo: implement this function 
      //       Called once when the parent game spawns object 
   }


   public void update() {
      // Check if waiting for input or click
      if(reading_input) wait_for_input();
      else wait_for_click();


   }

   private void wait_for_click(){
      // Check if the mouse is over the input box
      float x = sys.mouse_ui_x * GameEngine.SCREEN_WIDTH;
      float y = sys.mouse_ui_y * GameEngine.SCREEN_HEIGHT;
      float this_x = pos.x - width / 2f;
      float this_y = pos.y - height / 2f;

      mouse_over = (x >= this_x && x <= this_x + width && y >= this_y && y <= this_y + height);

      // Check if mouse was clicked
      if(mouse_over && !was_clicked && mouse_click.pressed){
         // Mouse clicked
         if(sys.input_manager.getInput(this::input_callback, max_input_length, input))
            // Was able to start reading input, this may not always be the
            // case. There could be other UIInputs reading input at this time
            reading_input = true;
      }

      was_clicked = mouse_click.pressed;
   }

   private void wait_for_input() {
      // Todo: Could do something where if the user clicks out of the input then its stops or something
      input = sys.input_manager.current_string;

      time_since_blink += sys.DELTA_TIME;

      if(time_since_blink > blink_time){
         time_since_blink = 0;
         show_underscore = !show_underscore;
      }
   }

   private void input_callback(String string, boolean was_escaped){
      callback.onInput(string);
      reading_input = false;
   }


   public void draw() {
      sys.pushUI();

      // Draw background
      sys.fill(rect_colour.x, rect_colour.y, rect_colour.z);
      sys.rectMode(PConstants.CENTER);
      sys.stroke(border_colour.x, border_colour.y, border_colour.z);
      sys.strokeWeight(border_width);
      sys.rect(pos.x, pos.y, width, height);

      // Draw text
      sys.textSize(text_size);

      sys.fill(text_colour.x, text_colour.y, text_colour.z);

      sys.textAlign(PConstants.LEFT);

      String text = prompt + input;
      if(reading_input && show_underscore) text += "_";

      sys.uiText(text, pos.x - width / 2f + border_width + padding, pos.y - text_height / 2);

      sys.rectMode(PConstants.CORNER);
      sys.popUI();
   }


   public void apply_scaling(){
      // Update width and height
      width *= GameEngine.UI_SCALE;
      height *= GameEngine.UI_SCALE;
      padding *= GameEngine.UI_SCALE;
   }


   public void reset_text_size(){
      // Get max height and width of text
      StringBuilder sb = new StringBuilder(prompt);

      for(int i = 0; i < max_input_length; i++)
         sb.append("W");

      String max_text = sb.toString();
      text_size = 2;

      sys.textSize(text_size);
      text_height = sys.textAscent() - sys.textDescent();

      while(text_height < height - (padding + border_width) && sys.textWidth(max_text) < width - (padding + border_width)){
         text_size += 1;
         sys.textSize(text_size);
         text_height = sys.textAscent() - sys.textDescent();
      }

      text_height -= 1;
   }

   public static interface CallBack {
      public void onInput(String input);
   }
}

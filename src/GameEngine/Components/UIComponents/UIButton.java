package GameEngine.Components.UIComponents;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.Managers.InputManager;
import processing.core.PConstants;
import processing.core.PVector;


public class UIButton extends Component {
   // Attributes
   public PVector text_colour;
   public PVector rect_colour;
   public PVector border_colour;
   public PVector hover_text_colour;
   public PVector hover_rect_colour;
   public PVector hover_border_colour;
   public boolean rect_fill;
   public PVector pos;
   public float width;
   public float height;
   public String text;
   public Icon icon_renderer;

   private float padding;
   private float border_width;
   private float text_height;
   private int text_size;
   public CallBack callback;

   public boolean mouse_over;
   private boolean was_clicked;
   private InputManager.Key mouse_click;



   // Constructor
   public UIButton(GameObject parent, CallBack callback, String text, PVector pos, PVector text_colour, PVector rect_colour, PVector border_colour, PVector hover_text_colour, PVector hover_rect_colour, PVector hover_border_colour, float padding, float border_width, float width, float height, boolean rect_fill) {
      this(parent, callback, text, null, pos, text_colour, rect_colour, border_colour, hover_text_colour, hover_rect_colour, hover_border_colour, padding, border_width, width, height, rect_fill);
   }


   public UIButton(GameObject parent, CallBack callback, Icon draw, PVector pos, PVector text_colour, PVector rect_colour, PVector border_colour, PVector hover_text_colour, PVector hover_rect_colour, PVector hover_border_colour, float padding, float border_width, float width, float height, boolean rect_fill) {
      this(parent, callback, "12345", draw, pos, text_colour, rect_colour, border_colour, hover_text_colour, hover_rect_colour, hover_border_colour, padding, border_width, width, height, rect_fill);
   }

   public UIButton(GameObject parent, CallBack callback, String text, Icon draw, PVector pos, PVector text_colour, PVector rect_colour, PVector border_colour, PVector hover_text_colour, PVector hover_rect_colour, PVector hover_border_colour, float padding, float border_width, float width, float height, boolean rect_fill) {
      super(parent);

      // Init attributes
      this.pos = pos;
      this.width = width;
      this.height = height;
      this.text = text;
      this.icon_renderer = draw;
      this.padding = padding;
      this.border_width = border_width;
      this.text_colour = text_colour;
      this.rect_colour = rect_colour;
      this.border_colour = border_colour;
      this.hover_border_colour = hover_border_colour;
      this.hover_rect_colour = hover_rect_colour;
      this.hover_text_colour = hover_text_colour;
      this.rect_fill = rect_fill;
      this.callback = callback;

      this.mouse_click = sys.input_manager.mouse_click;
      this.was_clicked = mouse_click.pressed;

      // set text size if needed
      apply_scaling();
      reset_text_size();
   }


   // Methods 
   public void start() {

   }


   public void update() {
      // Check if the mouse is over the button
      float x = sys.mouse_ui_x * GameEngine.SCREEN_WIDTH;
      float y = sys.mouse_ui_y * GameEngine.SCREEN_HEIGHT;
      float this_x = pos.x - width / 2f;
      float this_y = pos.y - height / 2f;

      mouse_over = (x >= this_x && x <= this_x + width && y >= this_y && y <= this_y + height);

      // Check if the mouse was clicked
      if(mouse_over && !was_clicked && mouse_click.pressed){
         // Mouse clicked
         callback.onClick();
      }

      was_clicked = mouse_click.pressed;
   }


   public void draw() {
      sys.pushUI();

      // Draw background
      if(rect_fill)
         if(mouse_over) sys.fill(hover_rect_colour.x, hover_rect_colour.y, hover_rect_colour.z);
         else sys.fill(rect_colour.x, rect_colour.y, rect_colour.z);
      else sys.noFill();

      sys.rectMode(PConstants.CENTER);

      if(mouse_over) sys.stroke(hover_border_colour.x, hover_border_colour.y, hover_border_colour.z);
      else sys.stroke(border_colour.x, border_colour.y, border_colour.z);

      sys.strokeWeight(border_width);
      sys.rect(pos.x, pos.y, width, height);

      // Draw text
      sys.textSize(text_size);

      if(mouse_over) sys.fill(hover_text_colour.x, hover_text_colour.y, hover_text_colour.z);
      else sys.fill(text_colour.x, text_colour.y, text_colour.z);

      sys.textAlign(PConstants.CENTER);
      if(icon_renderer != null) icon_renderer.draw(this);
      else sys.uiText(text, pos.x, pos.y - text_height / 2);

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
      // Get max height of text
      text_size = 2;

      sys.textSize(text_size);
      text_height = sys.textAscent() - sys.textDescent();

      while(text_height < height - (padding + border_width) && sys.textWidth(text) < width - (padding + border_width)){
         text_size += 1;
         sys.textSize(text_size);
         text_height = sys.textAscent() - sys.textDescent();
      }

      text_height -= 1;
   }

   public interface CallBack{
      public void onClick();
   }

   public interface Icon{
      public void draw(UIButton parent);
   }
}

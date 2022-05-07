package GameEngine.Components.UIComponents;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PConstants;
import processing.core.PVector;
import processing.event.Event;


public class UIControlEditor extends Component {
   // Attributes
   public static final int INTERNAL_PADDING = 10;

   public boolean can_update;

   public PVector pos;
   public PVector text_col;
   public PVector rect_col;
   public PVector border_col;
   public float width;
   public float height;
   public float text_width;
   public float text_height;
   public int control_text_size;
   public int event_text_size;

   public float control_text_height;
   public float event_text_height;
   public boolean hover;

   private boolean reading_key;
   private boolean can_click;

   private String control_name;
   private String event_name;

   private PVector text_col_no_hover;
   private PVector rect_col_no_hover;
   private PVector border_col_no_hover;
   private PVector text_col_hover;
   private PVector rect_col_hover;
   private PVector border_col_hover;
   private Event current_event;
   private UIButton.CallBack on_click;
   private UIButton.CallBack on_finish;

   public float rect_alpha;
   public float hover_rect_alpha;
   public float no_hover_rect_alpha;


   // Constructor
   public UIControlEditor(GameObject parent, UIButton.CallBack on_click, UIButton.CallBack on_finish, PVector pos, PVector text_col, PVector rect_col, PVector border_col, PVector text_hover_col, PVector rect_hover_col, PVector border_hover_col, float width, float height, float rect_alpha, float hover_rect_alpha) {
      super(parent);

      // Init attributes
      this.on_click = on_click;
      this.on_finish = on_finish;
      this.can_click = false;
      this.can_update = true;
      this.pos = pos;
      this.text_col = text_col;
      this.rect_col = rect_col;
      this.border_col = border_col;
      this.width = width * GameEngine.UI_SCALE;
      this.height = height * GameEngine.UI_SCALE;
      this.text_width = this.width / 2f - INTERNAL_PADDING * 2;
      this.text_height = this.height - INTERNAL_PADDING * 2;
      this.no_hover_rect_alpha = rect_alpha;
      this.rect_alpha = rect_alpha;
      this.hover_rect_alpha = hover_rect_alpha;

      this.text_col_no_hover = text_col;
      this.rect_col_no_hover = rect_col;
      this.border_col_no_hover = border_col;
      this.text_col_hover = text_hover_col;
      this.rect_col_hover = rect_hover_col;
      this.border_col_hover = border_hover_col;
   }


   // Methods 
   public void start() {}

   public void update() {
      // Manage updating the control key
      if(reading_key){
         // Manage reading the key
         if(sys.input_manager.latest_event != current_event){
            // Recivied input event
            sys.input_manager.setMapping(control_name, sys.input_manager.latest_event);
            sys.input_manager.reload();
            reading_key = false;
            can_click = false;
            on_finish.onClick();
            set_control(control_name);
         }
      }


      // Check if mouse over
      float x = sys.mouse_ui_x * GameEngine.SCREEN_WIDTH;
      float y = sys.mouse_ui_y * GameEngine.SCREEN_HEIGHT;
      hover = (x > pos.x && x < pos.x + width &&
               y > pos.y && y < pos.y + height);



      // Update colour
      update_colour();

      // Check if click
      update_click();
   }


   public void refresh(){
      set_control(control_name);
      can_click = false;
      can_update = true;
      reading_key = false;
   }


   public void draw() {
      sys.pushUI();
      sys.stroke(border_col.x, border_col.y, border_col.z);

      sys.fill(rect_col.x, rect_col.y, rect_col.z, rect_alpha);
      sys.rect(pos.x, pos.y, width, height);

      sys.textAlign(PConstants.CORNER);

      sys.textSize(control_text_size);
      sys.fill(text_col.x, text_col.y, text_col.z);
      sys.uiText(control_name, pos.x, pos.y + sys.textDescent());

      sys.textSize(event_text_size);
      sys.uiText(event_name, pos.x + width / 2f + INTERNAL_PADDING, pos.y + sys.textDescent());

      sys.popUI();
   }


   public void set_control(String control_name){
      this.control_name = control_name;
      this.event_name = sys.input_manager.getKey(control_name).string;

      reset_text_size();
   }


   private void update_colour(){
      if(hover) {
         rect_col = rect_col_hover;
         text_col = text_col_hover;
         border_col = border_col_hover;
         rect_alpha = hover_rect_alpha;
      } else {
         rect_col = rect_col_no_hover;
         text_col = text_col_no_hover;
         border_col = border_col_no_hover;
         rect_alpha = no_hover_rect_alpha;
      }
   }


   private void update_click(){
      if(reading_key || !can_update)
         return;

      if(hover && !can_click){
         can_click = !sys.input_manager.mouse_click.pressed;
      }else if(hover && sys.input_manager.mouse_click.pressed){
         current_event = sys.input_manager.latest_event;
         reading_key = true;
         on_click.onClick();
      }
   }

   private void reset_text_size(){
      // Get max height of text
      control_text_size = get_text_size(control_name, text_width, text_height);
      event_text_size = get_text_size(event_name, text_width, text_height);

      sys.textSize(control_text_size);
      control_text_height = sys.textAscent() + sys.textDescent();
      sys.textSize(event_text_size);
      event_text_height = sys.textAscent() + sys.textDescent();
   }


   private int get_text_size(String text, float width, float height){
      int size = 2;

      sys.textSize(size);
      float text_height = sys.textAscent() + sys.textDescent();

      while(text_height < height && sys.textWidth(text) < width){
         size += 1;
         sys.textSize(size);
         text_height = sys.textAscent() + sys.textDescent();
      }

      return size;
   }
}

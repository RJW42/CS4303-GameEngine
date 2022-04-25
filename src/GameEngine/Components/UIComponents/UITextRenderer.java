package GameEngine.Components.UIComponents;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PConstants;
import processing.core.PVector;


public class UITextRenderer extends Component {
   // Attributes
   public PVector text_pos;
   public PVector text_colour;
   public String text;
   public int text_align;
   public int max_width;
   public int max_height;
   public int text_size;
   public int text_alpha;

   // Constructor
   public UITextRenderer(GameObject parent, PVector text_pos, PVector text_colour, String text, int text_align, int max_width, int max_height) {
      super(parent);
      this.text_pos = text_pos;
      this.text_colour = text_colour;
      this.text = text;
      this.text_align = text_align;
      this.max_width = max_width * GameEngine.UI_SCALE;
      this.max_height = max_height * GameEngine.UI_SCALE;
      this.text_alpha = 300;

      reset_text_size();
   }


   // Methods 
   public void start() {}
   public void update() {}

   public void draw() {
      sys.pushUI();

      sys.textAlign(text_align);
      sys.textSize(text_size);
      sys.fill(text_colour.x, text_colour.y, text_colour.z, text_alpha);
      sys.uiText(text, text_pos.x, text_pos.y);
      sys.popUI();
   }

   public void set_text(String text){
      this.text = text;
      this.reset_text_size();
   }

   public void reset_text_size(){
      // Get max height of text
      // Todo: could maybe split text into mul lines if possible
      text_size = 2;

      sys.textSize(text_size);
      float text_height = sys.textAscent() - sys.textDescent();

      while(text_height < max_height && sys.textWidth(text) < max_width){
         text_size += 1;
         sys.textSize(text_size);
         text_height = sys.textAscent() - sys.textDescent();
      }
   }
}

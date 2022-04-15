package GameEngine.Components.MapBuildingComponents.Tools;


import GameEngine.Components.Component;
import GameEngine.Components.MapBuildingComponents.ToolSelector;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PConstants;


public abstract class Tool extends Component {
   // Attributes
   public String text;
   public ToolSelector selector;
   public int x;
   public int y;
   public int width;
   public int height;
   public int border_width;

   private boolean overlap;


   // Constructor
   public Tool(GameObject parent, String text) {
      super(parent);

      // Init attributes
      this.text = text;
   }


   // Methods
   public void check_hover(){
      // Check if the mouse overlaps with icon
      int x = (int)(sys.mouse_ui_x * GameEngine.SCREEN_WIDTH);
      int y = (int)(sys.mouse_ui_y * GameEngine.SCREEN_HEIGHT);

      overlap = x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
   }

   public void draw_icon() {
      sys.fill(0, 255, 0);
      sys.rectMode(PConstants.CORNER);
      sys.rect(x, y, width, height);
      sys.textAlign(PConstants.CENTER);
      sys.fill(0);
      sys.textSize(50);
      sys.uiText(text, x + width / 2, y + height / 2);
   };

   public void draw_border() {
      if(!overlap)
         return;

      sys.fill(255);
      sys.rect(x - border_width, y - border_width, width + 2 * border_width, height + 2 * border_width);
   }

   public void cancel(){

   }
}

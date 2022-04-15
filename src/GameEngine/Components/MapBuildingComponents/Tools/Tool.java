package GameEngine.Components.MapBuildingComponents.Tools;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import processing.core.PConstants;


public abstract class Tool extends Component {
   // Attributes
   public String text;
   public boolean active;
   public int x;
   public int y;
   public int width;
   public int height;


   // Constructor
   public Tool(GameObject parent, String text) {
      super(parent);

      // Init attributes
      this.text = text;
   }


   // Methods
   public void draw_icon() {
      sys.fill(0, 255, 0);
      sys.rect(x, y, width, height);
   };

   public void cancel(){

   }
}

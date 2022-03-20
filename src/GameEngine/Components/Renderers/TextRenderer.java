package GameEngine.Components.Renderers;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PVector;


public class TextRenderer extends Component {
   // Attributes
   public int size;
   public int text_align;
   public PVector color;
   public String text;
   public PVector offset;

   // Constructor
   public TextRenderer(GameObject parent, PVector offset) {
      super(parent);

      this.text = "";
      this.text_align = PApplet.LEFT;
      this.offset = offset;
      this.size = 20;
      this.color = new PVector(255, 255, 255);
   }


   // Methods 
   public void draw() {
      // Get the position for the text
      PVector pos = PVector.add(parent.pos, offset);

      // Draw text with desired settings
      sys.fill(color.x, color.y, color.z);
      sys.textSize(size);
      sys.textAlign(text_align);
      sys.text(text, pos.x, pos.y);
   }
}

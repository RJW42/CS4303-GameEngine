package GameEngine.Components.Renderers;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;


public class RectRenderer extends Component {
   // Attributes
   public PVector color;
   private float width;
   private float height;

   // Constructor
   public RectRenderer(GameObject parent, PVector color, float width, float height) {
      super(parent);
      this.width = width;
      this.height = height;
      this.color = color;
   }

   // Methods
   @Override
   public void draw() {
      parent.sys.fill(color.x, color.y, color.z);
      parent.sys.noStroke();
      parent.sys.rect(parent.pos.x, parent.pos.y, width, -height);
   }
}

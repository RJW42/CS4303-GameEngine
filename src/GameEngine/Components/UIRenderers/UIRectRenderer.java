package GameEngine.Components.UIRenderers;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;


public class UIRectRenderer extends Component {
   // Attributes
   public PVector color;
   private float width;
   private float height;

   // Constructor
   public UIRectRenderer(GameObject parent, PVector color, float width, float height) {
      super(parent);
      this.width = width;
      this.height = height;
      this.color = color;
   }

   // Methods
   @Override
   public void draw() {
      sys.pushUI();
      sys.fill(color.x, color.y, color.z);
      sys.noStroke();
      sys.rect(0, 0, 50, 50);
      sys.popUI();
   }
}

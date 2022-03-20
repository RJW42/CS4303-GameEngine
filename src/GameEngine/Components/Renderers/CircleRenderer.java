package GameEngine.Components.Renderers;

import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;

public class CircleRenderer extends Component {
   // Attributes
   public PVector color;
   private float extent;

   // Constructor
   public CircleRenderer(GameObject parent, PVector color, float extent) {
      super(parent);
      this.extent = extent;
      this.color = color;
   }

   // Methods
   @Override
   public void draw() {
      parent.sys.fill(color.x, color.y, color.z);
      parent.sys.noStroke();
      parent.sys.circle(parent.pos.x, parent.pos.y, extent * 2);
   }
}

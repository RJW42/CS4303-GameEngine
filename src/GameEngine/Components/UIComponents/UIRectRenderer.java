package GameEngine.Components.UIComponents;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PConstants;
import processing.core.PVector;


public class UIRectRenderer extends Component {
   // Attributes
   public PVector pos;
   public PVector rect_colour;
   public PVector border_colour;
   public float width;
   public float height;
   public float border_width;
   public boolean rect_fill;


   // Constructor
   public UIRectRenderer(GameObject parent, PVector pos, PVector rect_colour, PVector border_colour, float border_width, float width, float height, boolean rect_fill) {
      super(parent);

      // Init attributes
      this.pos = pos;
      this.width = width * GameEngine.UI_SCALE;
      this.height = height * GameEngine.UI_SCALE;
      this.border_width = border_width;
      this.rect_colour = rect_colour;
      this.border_colour = border_colour;
      this.rect_fill = rect_fill;
   }


   // Methods 
   public void start() {

   }

   public void update() {

   }

   public void draw() {
      sys.pushUI();

      // Draw background
      if(rect_fill) sys.fill(rect_colour.x, rect_colour.y, rect_colour.z);
      else sys.noFill();

      sys.rectMode(PConstants.CENTER);
      sys.stroke(border_colour.x, border_colour.y, border_colour.z);
      sys.strokeWeight(border_width);
      sys.rect(pos.x, pos.y, width, height);

      sys.rectMode(PConstants.CORNER);
      sys.popUI();
   }
}

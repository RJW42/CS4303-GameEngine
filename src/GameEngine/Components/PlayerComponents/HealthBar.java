package GameEngine.Components.PlayerComponents;


import GameEngine.Components.Component;
import GameEngine.Components.Damagable;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PConstants;
import processing.core.PVector;


public class HealthBar extends Component {
   // Attributes
   private static final PVector BORDER_COL   = new PVector(255, 255, 255);
   private static final PVector BACK_COL     = new PVector(127, 127, 127);
   private static final PVector FULL_COL     = new PVector(0, 255, 0);
   private static final PVector MID__COL     = new PVector(255, 165, 0);
   private static final PVector LOW_COL      = new PVector(255, 0, 0);

   private Damagable damagable;
   private PVector current_col;

   public PVector pos;
   public float width;
   public float height;
   public float border_width;
   private float percentage;


   // Constructor
   public HealthBar(GameObject parent, PVector pos, float width, float height) {
      super(parent);

      // Init attributes;
      this.pos = pos;
      this.width = width * GameEngine.UI_SCALE;
      this.height = height * GameEngine.UI_SCALE;
      this.border_width = 1f;
   }


   // Methods 
   public void start() {
      this.damagable = parent.getComponent(Damagable.class);
   }

   public void update() {
      // Check what health percentage at
      percentage = ((float) damagable.health) / damagable.starting_health;

      if(percentage < 0.33){
         current_col = LOW_COL;
      }else if(percentage < 0.66){
         current_col = MID__COL;
      }else {
         current_col = FULL_COL;
      }
   }

   public void draw() {
      sys.pushUI();
      sys.pushStyle();

      // Draw background

      sys.rectMode(PConstants.CORNER);
      sys.fill(BACK_COL.x, BACK_COL.y, BACK_COL.z);
      sys.stroke(BORDER_COL.x, BORDER_COL.y, BORDER_COL.z);
      sys.strokeWeight(border_width);
      sys.rect(pos.x, pos.y, width, height);

      sys.fill(current_col.x, current_col.y, current_col.z);
      sys.rect(pos.x, pos.y, width * percentage, height);

      sys.popStyle();
      sys.popUI();
   }
}

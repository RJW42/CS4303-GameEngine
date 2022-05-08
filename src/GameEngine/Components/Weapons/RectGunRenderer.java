package GameEngine.Components.Weapons;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PVector;


public class RectGunRenderer extends GunRenderer {
   // Attributes
   public static PVector BORDER_COL = new PVector(0, 0, 0);

   public float width;
   public float height;
   public float half_height;
   public PVector rect_col = new PVector(0, 0, 0);
   public PVector border_col = BORDER_COL;


   // Constructor
   public RectGunRenderer(GameObject parent, PVector offset, float width, float height) {
      super(parent, offset);

      // Init attributes
      this.width = width;
      this.height = height;
      this.half_height = height / 2f;
   }


   // Methods 
   public void start() {
   }

   public void update() {
   }

   public void draw() {
        sys.push();
        sys.stroke(border_col.x, border_col.y, border_col.z);
        sys.strokeWeight(2f / GameEngine.PIXEL_TO_METER);
        sys.translate(parent.pos.x + offset.x, parent.pos.y + offset.y);
        sys.rotate(gun.rotation_angle);
        sys.fill(rect_col.x, rect_col.y, rect_col.z);
        sys.rect(0, -half_height, width, height);
        sys.pop();
   }

   public void fire(){

   }
}

package GameEngine.Components.Weapons;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PVector;


public class RectGunRenderer extends GunRenderer {
   // Attributes
   public float width;
   public float height;
   public float half_height;


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
        sys.pushMatrix();
        sys.translate(parent.pos.x + offset.x, parent.pos.y + offset.y);
        sys.rotate(gun.rotation_angle);
        sys.fill(0);
        sys.rect(0, -half_height, width, height);
        sys.popMatrix();
   }

   public void fire(){

   }
}

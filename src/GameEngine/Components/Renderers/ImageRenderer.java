package GameEngine.Components.Renderers;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;


public class ImageRenderer extends Component {
   // Attributes
   public PVector offset;
   public PImage image;
   public String sprite_name;
   public int width_pixels, height_pixels;
   public float rotation_angle, width, height;

   // Constructor
   public ImageRenderer(GameObject parent, String sprite_name, float width, float height, PVector offset) {
      super(parent);

      this.sprite_name = sprite_name;
      this.width_pixels = Math.round(GameEngine.PIXEL_TO_METER_X * width);
      this.height_pixels = Math.round(GameEngine.PIXEL_TO_METER_Y * height);
      this.width = width;
      this.height = height;
      this.offset = offset;
      this.rotation_angle = 0;
   }


   // Methods 
   public void start() {
      // Load the image
      image = sys.sprite_manager.get_sprite(sprite_name, width_pixels, height_pixels);
   }

   public void update() {

   }

   public void draw() {
      // Get draw position
      PVector pos = PVector.add(offset, parent.pos);

      // Draw sprite
      sys.image(image, pos.x, pos.y, rotation_angle);
   }
}

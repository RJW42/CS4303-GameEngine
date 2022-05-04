package GameEngine.Components;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import processing.core.PImage;
import processing.core.PVector;


public class MovingSpriteManager extends Component {
   // Attributes
   public ForceManager force_manager;
   public PImage left;
   public PImage right;
   public PVector offset;
   public int width_pixels, height_pixels;
   public float width, height;

   private final String l_name;
   private final String r_name;
   private PImage current;

   // Constructor
   public MovingSpriteManager(GameObject parent, PVector offset, String l_name, String r_name, float width, float height) {
      super(parent);

      // Init attributes
      this.l_name = l_name;
      this.r_name = r_name;
      this.offset = offset;
      this.width_pixels = Math.round(GameEngine.PIXEL_TO_METER * width);
      this.height_pixels = Math.round(GameEngine.PIXEL_TO_METER * height);
      this.width = width;
      this.height = height;
   }


   // Methods 
   public void start() {
      force_manager = parent.getComponent(ForceManager.class);
      left = sys.sprite_manager.get_sprite(l_name, width_pixels, height_pixels);
      right = sys.sprite_manager.get_sprite(r_name, width_pixels, height_pixels);
      current = left;
   }

   public void update() {
      // Update current sprite
      if(force_manager.velocity.x > 0 && current == left){
         current = right;
      } else if(force_manager.velocity.x < 0 && current == right){
         current = left;
      }
   }

   public void draw() {
      // Get draw position
      PVector pos = PVector.add(offset, parent.pos);

      // Draw sprite
      sys.image(current, pos.x, pos.y, 0);
   }
}

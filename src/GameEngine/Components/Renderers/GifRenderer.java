package GameEngine.Components.Renderers;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.PGif;
import processing.core.PApplet;
import processing.core.PVector;


public class GifRenderer extends Component {
   // Attributes
   public PVector offset;
   public PGif gif;
   public String gif_name;
   public int width_pixels, height_pixels;
   public float rotation_angle, width, height;
   public boolean loop;
   public boolean start_finished;

   private int fps;
   private boolean triggered;

   // Constructor
   public GifRenderer(GameObject parent, String gif_name, int fps, float width, float height, PVector offset){
      this(parent, gif_name, fps, width, height, offset, true, false);
   }

   public GifRenderer(GameObject parent, String gif_name, int fps, float width, float height, PVector offset, float rotation_angle){
      this(parent, gif_name, fps, width, height, offset, true, false);
      this.rotation_angle = rotation_angle;
   }

   public GifRenderer(GameObject parent, String gif_name, int fps, float width, float height, PVector offset, boolean loop, boolean start_finished) {
      super(parent);

      this.gif_name = gif_name;
      this.width_pixels = Math.round(GameEngine.PIXEL_TO_METER * width);
      this.height_pixels = Math.round(GameEngine.PIXEL_TO_METER * height);
      this.width = width;
      this.height = height;
      this.offset = offset;
      this.fps = fps;
      this.rotation_angle = PApplet.HALF_PI;
      this.loop = loop;
      this.start_finished = start_finished;
      // this.on_complete = on_complete;
      this.triggered = false;
   }


   // Methods
   public void start() {
      // Load the image
      gif = sys.sprite_manager.get_gif(gif_name, width_pixels, height_pixels);
      gif.setFPS(fps);
      gif.setLooping(loop);
      gif.finished = start_finished;
   }

   public void update() {

   }

   public void draw() {
      // Get draw position
      PVector pos = PVector.add(offset, parent.pos);

      // Draw sprite
      gif.play(sys, pos, rotation_angle);

      // Check if finished
      if(gif.finished){ // && on_complete != null) {
         if(!triggered)
            // on_complete.onComplete(this);
         triggered = true;
      } else {
         triggered = false;
      }
   }
}

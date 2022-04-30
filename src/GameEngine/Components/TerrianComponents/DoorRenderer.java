package GameEngine.Components.TerrianComponents;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Core.Door;
import GameEngine.GameObjects.Core.Terrain;
import processing.core.PImage;


public class DoorRenderer extends Component {
   // Attributes
   private final Door door;
   private final float half_height;
   private final float half_width;
   private final PImage open;
   private final PImage close;


   // Constructor
   public DoorRenderer(Door parent, String opened, String closed) {
      super(parent);

      // init attributes
      this.half_height = Terrain.CELL_SIZE * 3f / 2f;
      this.half_width = Terrain.CELL_SIZE / 2f;

      this.door = parent;
      int width = (int) (Terrain.CELL_SIZE * GameEngine.PIXEL_TO_METER);
      int height = (int)(Terrain.CELL_SIZE * 3 * GameEngine.PIXEL_TO_METER);
      this.open = sys.sprite_manager.get_sprite(opened, width, height);
      this.close = sys.sprite_manager.get_sprite(closed, width, height);
   }


   // Methods 
   public void start() {

   }
   public void update() {}

   public void draw() {
      float x = parent.pos.x + half_width;
      float y = parent.pos.y - half_height;

      if (door.is_open) sys.image(open, x, y);
      else sys.image(close, x, y);
   }
}

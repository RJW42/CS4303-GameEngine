package GameEngine.Components.TerrianComponents;


import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Core.Door;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.GameObjects.GameObject;
import processing.core.PVector;


public class DoorRenderer extends Component {
   // Attributes
   public static final PVector COLOUR = new PVector();
   private final float width;
   private final float height;

   private final Door door;


   // Constructor
   public DoorRenderer(Door parent) {
      super(parent);

      // init attributes
      this.door = parent;
      this.width = Terrain.CELL_SIZE;
      this.height = Terrain.CELL_SIZE * 3;
   }


   // Methods 
   public void start() {}
   public void update() {}

   public void draw() {
      sys.stroke(COLOUR.x, COLOUR.y, COLOUR.z);
      sys.strokeWeight(0.1f);
      sys.noFill();

      if(door.is_open) sys.rect(parent.pos.x, parent.pos.y, width, -width);
      else sys.rect(parent.pos.x, parent.pos.y, width, -height);

      sys.strokeWeight(1 / GameEngine.PIXEL_TO_METER);
   }
}

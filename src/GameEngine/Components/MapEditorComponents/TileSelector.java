package GameEngine.Components.MapEditorComponents;


import GameEngine.Components.Component;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Core.Terrain;


public class TileSelector extends Component {
   // Attributes
   public int current_index;
   public int current_x;
   public int current_y;

   private TerrainGenerator generator;


   // Constructor
   public TileSelector(GameObject parent) {
      super(parent);

      // Init attributes
      current_index = -1;
   }


   // Methods 
   public void start() {
       generator = sys.terrain.getComponent(TerrainGenerator.class);
   }

   public void update() {
      // Get the current index from the mouse position
      int x = ((int) sys.mouse_x) / Terrain.CELL_SIZE;
      int y = ((int) sys.mouse_y) / Terrain.CELL_SIZE;

      if(!generator.validWalkCord(x, y)){
         current_index = -1;
         return;
      }

      current_index = generator.getIndex(x, y);
      current_x = x;
      current_y = y;
   }

   public void draw() {
      sys.fill(255, 100f);
      sys.rect(current_x ,current_y, Terrain.CELL_SIZE, Terrain.CELL_SIZE);
   }
}

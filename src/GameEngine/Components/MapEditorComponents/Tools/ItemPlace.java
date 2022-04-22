package GameEngine.Components.MapEditorComponents.Tools;


import GameEngine.Components.Component;
import GameEngine.Components.MapEditorComponents.TileSelector;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.Utils.Managers.InputManager;


public class ItemPlace extends Tool {
   // Attributes
   private ItemSelect item_select;
   private TileSelector tile_selector;
   private InputManager.Key click;

   private TerrainGenerator generator;
   private TerrainRenderer renderer;

   private int prev_x;
   private int prev_y;

   // Constructor
   public ItemPlace(GameObject parent) {
      super(parent);

      // Init attributes
      this.icon_text = "PL";
   }


   // Methods 
   public void start() {
      this.item_select = parent.getComponent(ItemSelect.class);
      this.tile_selector = parent.getComponent(TileSelector.class);
      this.click = sys.input_manager.mouse_click;

      this.generator = sys.terrain.getComponent(TerrainGenerator.class);
      this.renderer = sys.terrain.getComponent(TerrainRenderer.class);
   }

   public void update() {
      // Check if should place tile
      if(!active){
         prev_x = -1;
         prev_y = -1;
         return;
      }

      if(menu.mouse_hovering || !click.pressed || (prev_x == tile_selector.current_x && prev_y == tile_selector.current_y))
         return;

      // Can place tile
      place_tile();
   }

   private void place_tile(){
      // Get location to place
      prev_x = tile_selector.current_x;
      prev_y = tile_selector.current_y;

      generator.getWorld()[generator.getIndex(prev_x, prev_y)] = (item_select.current_item == Item.WALL) ? 1 : 0;
      renderer.resetMasks();
   }

   public void draw() {
      // Todo: implement this function 
      //       Called every frame during draw stage  
   }
}

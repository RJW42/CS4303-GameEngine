package GameEngine.Components.MapEditorComponents.Tools;


import GameEngine.Components.Component;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.Components.TerrianComponents.TerrainLoader;
import GameEngine.Components.TerrianComponents.TerrainRenderer;
import GameEngine.GameObjects.GameObject;
import GameEngine.Levels.MapBuilder;


public class RandomColour extends Tool {
   // Attributes
   private TerrainRenderer renderer;

   // Constructor
   public RandomColour(GameObject parent) {
      super(parent);

      // Init Attributes
      this.icon_text = "Colour";
   }


   // Methods
   public void start() {
      renderer = sys.terrain.getComponent(TerrainRenderer.class);
   }
   public void draw() {}

   public void update() {
      if(!active)
         return;

      // Pick colour, and deactivate
      // Todo: maybe add some cool down
      active = false;
      renderer.resetColours();
   }
}

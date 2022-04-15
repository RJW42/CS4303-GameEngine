package GameEngine.Components.MapBuildingComponents.Tools;


import GameEngine.Components.Component;
import GameEngine.Components.MapBuildingComponents.WorldEditor;
import GameEngine.GameObjects.GameObject;


public class ChangeTileType extends Tool {
   // Attributes
   public WorldEditor.TileTypes current_type;


   // Constructor
   public ChangeTileType(GameObject parent) {
      super(parent, "TL");
   }


   // Methods 
   public void start() {
      current_type = WorldEditor.TileTypes.WALL;
   }

   public void update() {

   }

   public void draw() {

   }
}

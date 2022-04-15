package GameEngine.Components.MapBuildingComponents;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;


public class WorldEditor extends Component {
   // Attributes


   // Constructor
   public WorldEditor(GameObject parent) {
      super(parent);
   }


   // Methods 
   public void start() {

   }

   public void PlaceEntity(TileTypes tile, float x, float y){

   }

   // Tracks all possible entities which can be placed on the world
   public enum TileTypes {
      AIR,
      WALL
   }

}

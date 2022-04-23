package GameEngine.Components.TerrianComponents;


import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;


public class AIPathManager extends Component {
   // Attributes
   private TerrainGenerator generator;

   // Constructor
   public AIPathManager(GameObject parent) {
      super(parent);

      // Init attributes

   }


   // Methods 
   public void start() {
       this.generator = parent.getComponent(TerrainGenerator.class);
   }

   public void update() {
   }

   public void draw() {
   }

   private void init_walkable_areas(){
      // Todo: implement
   }

   // Todo: implement some kind of a star search maybe
}

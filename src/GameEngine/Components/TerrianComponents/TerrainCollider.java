package GameEngine.Components.TerrianComponents;

import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.Components.Component;
import GameEngine.GameObjects.Core.Door;
import GameEngine.GameObjects.Core.Terrain;

public class TerrainCollider extends Component {
   // Attributes
   public RectCollisionComponent[] colliders;
   public boolean created_collisions;

   private TerrainGenerator generator;


   // Constructor
   public TerrainCollider(Terrain parent) {
      super(parent);

      // Init attributes
      created_collisions = false;
   }

   // Methods
   @Override
   public void start() {
      generator = parent.getComponent(TerrainGenerator.class);
   }

   @Override
   public void update() {
      if(!created_collisions)
         create_collisions();
   }


   private void create_collisions(){
      created_collisions = true;

      // Create collision components
      int[] world = generator.getWorld();
      colliders = new RectCollisionComponent[world.length];

      for(int x = 0; x < Terrain.WIDTH; x++){
         for(int y = 0; y < Terrain.HEIGHT; y++){
            // Check if this is an edge object
            if(world[generator.getIndex(x, y)] == Terrain.WALL && is_edge(x, y, world)) {
               create_collision(x, y); // Is an edge make it collidable
            }
         }
      }

      // Created all collision components can now spawn doors
      spawn_doors();
   }


   public void spawn_doors(){
      // Create all doors
      for(int x = 0; x < Terrain.WIDTH; x++){
         for(int y = 0; y < Terrain.HEIGHT; y++){
            // Check if this is a door object
            if(generator.getSpecialTiles()[generator.getIndex(x, y)] == Terrain.BASIC_DOOR_START)
               // It is spawn door
               sys.spawn(
                  new Door(sys, true, x, y,
                          colliders[generator.getIndex(x, y + 2)],
                          colliders[generator.getIndex(x, y + 1)],
                          colliders[generator.getIndex(x, y)]), 2
               );
            if(generator.getSpecialTiles()[generator.getIndex(x, y)] == Terrain.KILL_DOOR_START) {
               // Create kill door
               Door door =  new Door(sys, false, x, y,
                               colliders[generator.getIndex(x, y + 2)],
                               colliders[generator.getIndex(x, y + 1)],
                               colliders[generator.getIndex(x, y)]
               );

               // Give the generator access to this door, allows
               // the door to know when all monsters have been killed
               generator.add_kill_door(door);

               sys.spawn(door, 2);
            }
         }
      }
   }


   private void create_collision(int x, int y){
      // Case parent to access specific attributes
      Terrain parent = (Terrain) this.parent;

      // Make collision component
      RectCollisionComponent comp = new RectCollisionComponent(parent, null, Terrain.CELL_SIZE);
      comp.stationary = true;
      comp.offset.x = x * Terrain.CELL_SIZE;
      comp.offset.y = ((y + 1) * Terrain.CELL_SIZE);
      comp.start();

      parent.addComponent(comp);
      parent.collision_components.add(comp);

      colliders[generator.getIndex(x, y)] = comp;
   }


   private boolean position_valid(int x, int y){
      return x >= 0 && y >= 0 && x < Terrain.WIDTH && y < Terrain.HEIGHT;
   }


   private boolean is_edge(int x, int y, int[] world){
      if(position_valid(x - 1, y) && world[generator.getIndex(x - 1, y)] == Terrain.AIR){
         return true;
      }
      if(position_valid(x + 1, y) && world[generator.getIndex(x + 1, y)] == Terrain.AIR){
         return true;
      }
      if(position_valid(x, y - 1) && world[generator.getIndex(x, y - 1)] == Terrain.AIR){
         return true;
      }
      return position_valid(x, y + 1) && (
              world[generator.getIndex(x, y + 1)] == Terrain.AIR ||
              generator.getSpecialTiles()[generator.getIndex(x, y + 1)] == Terrain.BASIC_DOOR_START ||
              generator.getSpecialTiles()[generator.getIndex(x, y + 1)] == Terrain.KILL_DOOR_START);
   }
}

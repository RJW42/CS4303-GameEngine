package GameEngine.Components.TerrianComponents;

import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Terrain;
import processing.core.PConstants;
import processing.core.PVector;

import java.util.Random;

public class TerrainRenderer extends Component {
   // Attributes
   private static final int TOP_MASK         = 1;
   private static final int LEFT_MASK        = 2;
   private static final int RIGHT_MASK       = 4;
   private static final int BOTTOM_MASK      = 8;
   private static final int TOP_LEFT_MASK    = 16;
   private static final int TOP_RIGHT_MASK   = 32;
   private static final int BOTTOM_LEFT_MASK = 64;
   private static final int BOTTOM_RIGHT_MASK= 128;

   public static  final float BORDER_WIDTH   = 0.15f;

   private static int r_mask = 255;
   private static int g_mask = 65280;
   private static int b_mask = 16711680;

   private PVector air_colour       = new PVector(146, 153, 156);
   private PVector border_colour    = new PVector(151, 186, 201);
   private PVector wall_colour      = new PVector(17, 25, 28);
   private boolean random_colour    = true;
   private TerrainGenerator generator;
   private int[] world;
   private int[] world_masks;

   // Constructor
   public TerrainRenderer(GameObject parent) {
      super(parent);
   }

   // Methods
   @Override
   public void start() {
      // Get the world
      generator = this.parent.getComponent(TerrainGenerator.class);
      world = generator.getWorld();
      world_masks = new int[world.length];

      // Init colours
      resetColours();


      // Set the world masks
      resetMasks();
   }

   @Override
   public void update() {

   }

   public void resetMasks(){
      for(int x = 0; x < Terrain.WIDTH; x++)
         for(int y = 0; y < Terrain.HEIGHT; y++)
            world_masks[generator.getIndex(x, y)] = getMasks(x, y);
   }

   public void resetColours(){
      int h = new Random().nextInt(360);
      if(random_colour) {
         air_colour = hsl_colour(h, 1f, 0.75f);
         border_colour = hsl_colour(h, 1f, 0.6f);
         wall_colour = hsl_colour(h, 1f, 0.1f);
      }
   }

   @Override
   public void draw() {
      // Ensure rect mode is correct
      sys.rectMode(PConstants.CORNER);

      // Draw Each Cell
      for(int x = 0; x < Terrain.WIDTH; x++){
         for(int y = 0; y < Terrain.HEIGHT; y++){
            // Get index for this position then draw
            int index = generator.getIndex(x, y);

            switch (world[index]){
               case Terrain.WALL:
                  drawWall(x, y);
                  break;
               case Terrain.AIR:
                  drawAir(x, y);
                  break;
               default:
                  drawDebug(x, y, world[index]);
            }
         }
      }

      // Draw the masks over the cells
      for(int x = 0; x < Terrain.WIDTH; x++){
         for(int y = 0; y < Terrain.HEIGHT; y++){
            // Get index for this position then draw
            int index = generator.getIndex(x, y);
            if(world[index] == Terrain.WALL)
               drawMask(x, y, world_masks[index]);
         }
      }
   }

   private void drawWall(float x, float y){
      // Init x, y
      x = (x / Terrain.WIDTH) * GameEngine.WORLD_WIDTH;
      y = (y / Terrain.HEIGHT) * GameEngine.WORLD_HEIGHT;

      // Draw wall
      sys.fill(wall_colour.x, wall_colour.y, wall_colour.z);
      sys.noStroke();
      sys.square(x, y, (float)GameEngine.WORLD_WIDTH / Terrain.WIDTH);
   }

   private void drawMask(float x, float y, int mask){
      // Init x, y
      x = (x / Terrain.WIDTH) * GameEngine.WORLD_WIDTH;
      y = (y / Terrain.HEIGHT) * GameEngine.WORLD_HEIGHT;

      // Draw mask
      sys.fill(border_colour.x, border_colour.y, border_colour.z);
      if((mask & TOP_MASK) == TOP_MASK)
         sys.rect(x, y + Terrain.CELL_SIZE - BORDER_WIDTH, Terrain.CELL_SIZE, BORDER_WIDTH);
      if((mask & BOTTOM_MASK) == BOTTOM_MASK)
         sys.rect(x, y, Terrain.CELL_SIZE, BORDER_WIDTH);
      if((mask & LEFT_MASK) == LEFT_MASK)
         sys.rect(x, y, BORDER_WIDTH, Terrain.CELL_SIZE);
      if((mask & RIGHT_MASK) == RIGHT_MASK)
         sys.rect(x + Terrain.CELL_SIZE - BORDER_WIDTH, y, BORDER_WIDTH, Terrain.CELL_SIZE);
      sys.noStroke();
      if((mask & TOP_LEFT_MASK) == TOP_LEFT_MASK)
         sys.arc(x, y + Terrain.CELL_SIZE, BORDER_WIDTH * 2, BORDER_WIDTH * 2, PConstants.PI, PConstants.TWO_PI - PConstants.HALF_PI);
      if((mask & TOP_RIGHT_MASK) == TOP_RIGHT_MASK)
         sys.arc(x + Terrain.CELL_SIZE, y + Terrain.CELL_SIZE, BORDER_WIDTH * 2, BORDER_WIDTH * 2, PConstants.HALF_PI + PConstants.PI, PConstants.TWO_PI);
      if((mask & BOTTOM_LEFT_MASK) == BOTTOM_LEFT_MASK)
         sys.arc(x, y + Terrain.CELL_SIZE, BORDER_WIDTH * 2, BORDER_WIDTH * 2, 0, PConstants.HALF_PI);
      if((mask & BOTTOM_RIGHT_MASK) == BOTTOM_RIGHT_MASK)
         sys.arc(x, y, BORDER_WIDTH * 2, BORDER_WIDTH * 2, PConstants.HALF_PI, PConstants.PI);
   }


   private void drawAir(float x, float y){
      sys.fill(air_colour.x, air_colour.y, air_colour.z);
      sys.noStroke();
      sys.square((x / Terrain.WIDTH) * GameEngine.WORLD_WIDTH, (y / Terrain.HEIGHT) * GameEngine.WORLD_HEIGHT, (float)GameEngine.WORLD_WIDTH / Terrain.WIDTH);
   }


   private void drawDebug(float x, float y, int colour){
      PVector c = int_to_rgb(colour);
      sys.fill(c.x, c.y, c.z);
      sys.noStroke();
      sys.square((x / Terrain.WIDTH) * GameEngine.WORLD_WIDTH, (y / Terrain.HEIGHT) * GameEngine.WORLD_HEIGHT, (float)GameEngine.WORLD_WIDTH / Terrain.WIDTH);
   }


   private int getMasks(int x, int y){
      // Init masks
      int masks = 0;

      // Add each edge
      if(isAir(x, y + 1))
         masks = masks | TOP_MASK;
      if(isAir(x, y - 1))
         masks = masks | BOTTOM_MASK;
      if(isAir(x - 1, y))
         masks = masks | LEFT_MASK;
      if(isAir(x + 1, y))
         masks = masks | RIGHT_MASK;
      if(isAir(x, y + 1) && !isAir(x - 1, y + 1))
         masks = masks | TOP_LEFT_MASK;
      if(isAir(x, y + 1) && !isAir(x + 1, y + 1))
         masks = masks | TOP_RIGHT_MASK;
      if(isAir(x - 1, y) && !isAir(x - 1, y + 1))
         masks = masks | BOTTOM_LEFT_MASK;
      if(isAir(x, y - 1) && !isAir(x - 1, y - 1))
         masks = masks | BOTTOM_RIGHT_MASK;

      // Finished
      return masks;
   }


   private boolean isAir(int x, int y){
      return (generator.validWalkCord(x, y) && world[generator.getIndex(x, y)] == Terrain.AIR);
   }


   private static int random_colour(){
      // Init attributes
      float r, g, b;
      int h = new Random().nextInt(360);
      float s = 1f;
      float l = 0.75f;

      float c = (1-Math.abs(2*l - 1))*s;
      float x = c*(1-Math.abs((h/60f)%2 - 1));
      float m = l-c/2f;

      if(h <= 60){
         r = c; g = x; b = 0;
      }else if(h <= 120){
         r  = x; g = c; b = 0;
      }else if(h <= 180){
         r = 0; g = x; b = x;
      }else if(h <= 240){
         r = 0; g = x; b = c;
      }else if(h >= 300){
         r = x; g = 0; b = c;
      }else{
         r =  c; g = 0; b = x;
      }
      r = (r+m)*255;
      g = (g+m)*255;
      b = (b+m)*255;

      // return colour;
      return rgb_to_int((int)r, (int)g, (int)b);
   }


   public static PVector hsl_colour(int h, float s, float l){
      float r,g,b;
      float c = (1-Math.abs(2*l - 1))*s;
      float x = c*(1-Math.abs((h/60f)%2 - 1));
      float m = l-c/2f;

      if(h <= 60){
         r = c; g = x; b = 0;
      }else if(h <= 120){
         r  = x; g = c; b = 0;
      }else if(h <= 180){
         r = 0; g = x; b = x;
      }else if(h <= 240){
         r = 0; g = x; b = c;
      }else if(h >= 300){
         r = x; g = 0; b = c;
      }else{
         r =  c; g = 0; b = x;
      }
      r = (r+m)*255;
      g = (g+m)*255;
      b = (b+m)*255;

      return new PVector(r, g, b);
   }


   public static int rgb_to_int(int r, int g, int b){
      return (r & r_mask) | ((g << 8 & g_mask) | ((b << 16) & b_mask));
   }

   public static PVector int_to_rgb(int i){
      return new PVector(i & r_mask, (i & g_mask) >> 8, (i & b_mask) >> 16);
   }
}

package GameEngine.Components.TerrianComponents;

import GameEngine.Components.Component;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.Utils.PGif;
import processing.core.*;

import java.util.ArrayList;
import java.util.Random;

public class TerrainRenderer extends Component {
   // Attributes
   public static final PVector LAVA_COLOUR   = new PVector(221, 64, 15);

   private static final int TOP_MASK         = 1;
   private static final int LEFT_MASK        = 2;
   private static final int RIGHT_MASK       = 4;
   private static final int BOTTOM_MASK      = 8;
   private static final int TOP_LEFT_MASK    = 16;
   private static final int TOP_RIGHT_MASK   = 32;
   private static final int BOTTOM_LEFT_MASK = 64;
   private static final int BOTTOM_RIGHT_MASK= 128;

   public static final float BORDER_WIDTH    = 0.15f;
   public static final float OVERLAP_AMOUNT  = 0.01f;

   private static final boolean RANDOM_COLOUR= true;

   private static final int r_mask = 255;
   private static final int g_mask = 65280;
   private static final int b_mask = 16711680;

   public PVector air_colour       = new PVector(146, 153, 156);
   public PVector border_colour    = new PVector(151, 186, 201);
   public PVector wall_colour      = new PVector(17, 25, 28);

   private TerrainGenerator generator;
   private PGif lava;

   private final ArrayList<Air> air_blocks;
   private final ArrayList<Lava> lava_blocks;
   private final ArrayList<Wall> wall_blocks;
   private final ArrayList<RectMask> rect_masks;
   private final ArrayList<EdgeMask> edge_masks;

   private int[] world;
   private int[] tile_attributes;
   private int[] world_masks;

   // Constructor
   public TerrainRenderer(GameObject parent) {
      super(parent);

      // Init attributes
      lava_blocks = new ArrayList<>();
      air_blocks = new ArrayList<>();
      wall_blocks = new ArrayList<>();
      rect_masks = new ArrayList<>();
      edge_masks = new ArrayList<>();
   }

   // Methods
   @Override
   public void start() {
      // Get lava asset
      lava = sys.sprite_manager.get_gif("lava", (int)GameEngine.PIXEL_TO_METER + 2, (int)GameEngine.PIXEL_TO_METER + 2);

      lava.setLooping(true);
      lava.setFPS(4);

      // Get the world
      generator = this.parent.getComponent(TerrainGenerator.class);
      world = generator.getWorld();
      tile_attributes = generator.getSpecialTiles();
      world_masks = new int[world.length];

      // Init colours and masks
      reset();
   }

   public void update() {
   }


   @Override
   public void draw() {
      draw_air();
      draw_lava();
      draw_walls();
      draw_masks();
   }


   public void reset(){
      air_blocks.clear();
      lava_blocks.clear();
      wall_blocks.clear();
      edge_masks.clear();
      rect_masks.clear();

      reset_masks();
      create_graphics();
   }


   public void resetColours(){
      int h = new Random().nextInt(360);
      if(RANDOM_COLOUR) {
         air_colour = hsl_colour(h, 1f, 0.75f);
         border_colour = hsl_colour(h, 1f, 0.6f);
         wall_colour = hsl_colour(h, 1f, 0.1f);
      }
   }


   private void draw_air(){
      // Todo: could make this one big rectangle
      sys.fill(air_colour.x, air_colour.y, air_colour.z);
      sys.noStroke();

      for(Air air : air_blocks) {
         sys.square(air.x, air.y, air.width);
      }
   }


   private void draw_lava(){
      sys.fill(LAVA_COLOUR.x, LAVA_COLOUR.y, LAVA_COLOUR.z);
      sys.noStroke();

      for(Lava lava : lava_blocks) {
         if(lava.is_top){
            lava.gif.play(sys, new PVector(lava.x + Terrain.CELL_SIZE / 2f, lava.y + Terrain.CELL_SIZE / 2f), 0);
         } else {
            sys.square(lava.x, lava.y, lava.width);
         }
      }
   }


   private void draw_walls(){
      sys.fill(wall_colour.x, wall_colour.y, wall_colour.z);
      sys.noStroke();

      for(Wall wall : wall_blocks){
         sys.square(wall.x, wall.y, wall.width);
      }
   }


   private void draw_masks(){
      sys.fill(border_colour.x, border_colour.y, border_colour.z);
      sys.noStroke();

      for(RectMask rect : rect_masks){
         sys.rect(rect.x, rect.y, rect.height, rect.width);
      }

      for(EdgeMask edge : edge_masks){
         sys.arc(edge.x, edge.y, edge.width, edge.height, edge.start_rad, edge.end_rad);
      }
   }


   private void create_graphics() {
      // Loop through each index in the world and create a graphics object for it
      // Todo: need to change this when adding doors
      for(int x = 0; x < Terrain.WIDTH; x++){
         for(int y = 0; y < Terrain.HEIGHT; y++){
            // Get index for this position then draw
            if(is_air(x, y) || is_door(x, y)) {
               if(is_lava(x, y)) lava_blocks.add(create_lava(x, y));
               else air_blocks.add(create_air(x, y));
            }else wall_blocks.add(create_wall(x, y));
         }
      }

      // Create the masks
      for(int x = 0; x < Terrain.WIDTH; x++){
         for(int y = 0; y < Terrain.HEIGHT; y++){
            // Get index for this position then draw
            int index = generator.getIndex(x, y);
            if(world[index] == Terrain.WALL)
               crete_mask(x, y, world_masks[index]);
         }
      }

      rect_masks.forEach(r -> {
         r.x -= OVERLAP_AMOUNT;
         r.y -= OVERLAP_AMOUNT;
         r.width += OVERLAP_AMOUNT * 2;
         r.height += OVERLAP_AMOUNT * 2;
      });

      // Todo: check this and make work
//      edge_masks.forEach(e -> {
//         e.width += OVERLAP_AMOUNT;
//         e.height += OVERLAP_AMOUNT;
//      });
   }


   private void reset_masks(){
      for(int x = 0; x < Terrain.WIDTH; x++)
         for(int y = 0; y < Terrain.HEIGHT; y++)
            world_masks[generator.getIndex(x, y)] = get_masks(x, y);
   }


   private Air create_air(float x, float y){
      return new Air(
              (x / Terrain.WIDTH) * GameEngine.WORLD_WIDTH - OVERLAP_AMOUNT,
              (y / Terrain.HEIGHT) * GameEngine.WORLD_HEIGHT - OVERLAP_AMOUNT,
              (float)GameEngine.WORLD_WIDTH / Terrain.WIDTH + OVERLAP_AMOUNT * 2f
      );
   }


   private Lava create_lava(float x, float y){
      Lava lava = new Lava(
              (x / Terrain.WIDTH) * GameEngine.WORLD_WIDTH - OVERLAP_AMOUNT,
              (y / Terrain.HEIGHT) * GameEngine.WORLD_HEIGHT - OVERLAP_AMOUNT,
              (float)GameEngine.WORLD_WIDTH / Terrain.WIDTH + OVERLAP_AMOUNT * 2f,
              is_air((int)x, (int)y + 1) && !is_lava((int)x, (int)y + 1),
              this.lava
      );

      if(lava.is_top){
         air_blocks.add(create_air(x, y));
      }

      return lava;
   }


   private Wall create_wall(float x, float y){
      // Init x, y
      x = (x / Terrain.WIDTH) * GameEngine.WORLD_WIDTH;
      y = (y / Terrain.HEIGHT) * GameEngine.WORLD_HEIGHT;

      // Draw wall
      return new Wall(
              x - OVERLAP_AMOUNT,
              y - OVERLAP_AMOUNT,
              (float)GameEngine.WORLD_WIDTH / Terrain.WIDTH + OVERLAP_AMOUNT * 2f
      );
   }


   private void crete_mask(float x, float y, int mask){
      // Init x, y
      x = (x / Terrain.WIDTH) * GameEngine.WORLD_WIDTH;
      y = (y / Terrain.HEIGHT) * GameEngine.WORLD_HEIGHT;

      // Draw mask
      if((mask & TOP_MASK) == TOP_MASK)
         rect_masks.add(new RectMask(x, y + Terrain.CELL_SIZE - BORDER_WIDTH, Terrain.CELL_SIZE, BORDER_WIDTH));
      if((mask & BOTTOM_MASK) == BOTTOM_MASK)
         rect_masks.add(new RectMask(x, y, Terrain.CELL_SIZE, BORDER_WIDTH));
      if((mask & LEFT_MASK) == LEFT_MASK)
         rect_masks.add(new RectMask(x, y, BORDER_WIDTH, Terrain.CELL_SIZE));
      if((mask & RIGHT_MASK) == RIGHT_MASK)
         rect_masks.add(new RectMask(x + Terrain.CELL_SIZE - BORDER_WIDTH, y, BORDER_WIDTH, Terrain.CELL_SIZE));

      if((mask & TOP_LEFT_MASK) == TOP_LEFT_MASK)
         edge_masks.add(new EdgeMask(x, y + Terrain.CELL_SIZE, BORDER_WIDTH * 2, BORDER_WIDTH * 2, PConstants.PI, PConstants.TWO_PI - PConstants.HALF_PI));
      if((mask & TOP_RIGHT_MASK) == TOP_RIGHT_MASK)
         edge_masks.add(new EdgeMask(x + Terrain.CELL_SIZE, y + Terrain.CELL_SIZE, BORDER_WIDTH * 2, BORDER_WIDTH * 2, PConstants.HALF_PI + PConstants.PI, PConstants.TWO_PI));
      if((mask & BOTTOM_LEFT_MASK) == BOTTOM_LEFT_MASK)
         edge_masks.add(new EdgeMask(x, y + Terrain.CELL_SIZE, BORDER_WIDTH * 2, BORDER_WIDTH * 2, 0, PConstants.HALF_PI));
      if((mask & BOTTOM_RIGHT_MASK) == BOTTOM_RIGHT_MASK)
         edge_masks.add(new EdgeMask(x, y, BORDER_WIDTH * 2, BORDER_WIDTH * 2, PConstants.HALF_PI, PConstants.PI));
   }


   private int get_masks(int x, int y){
      // Init masks
      int masks = 0;

      // Check if block can be grappled or is door as it has no masks
      if(!is_hookable(x, y) || is_door(x, y))
         return masks;

      // Add each edge
      if(is_air(x, y + 1) || is_door(x, y + 1))
         masks = masks | TOP_MASK;
      if(is_air(x, y - 1) || is_door(x, y - 1))
         masks = masks | BOTTOM_MASK;
      if(is_air(x - 1, y) || is_door(x - 1, y))
         masks = masks | LEFT_MASK;
      if(is_air(x + 1, y) || is_door(x + 1, y))
         masks = masks | RIGHT_MASK;
      if(is_air(x, y + 1) && is_clean_wall(x - 1, y + 1))
         masks = masks | TOP_LEFT_MASK;
      if(is_air(x, y + 1) && is_clean_wall(x + 1, y + 1))
         masks = masks | TOP_RIGHT_MASK;
      if(is_air(x - 1, y) && is_clean_wall(x - 1, y + 1))
         masks = masks | BOTTOM_LEFT_MASK;
      if(is_air(x, y - 1) && is_clean_wall(x - 1, y - 1))
         masks = masks | BOTTOM_RIGHT_MASK;

      // Finished
      return masks;
   }


   private boolean is_clean_wall(int x, int y){
      return !is_air(x, y) && is_hookable(x, y) && !is_door(x, y);
   }


   private boolean is_air(int x, int y){
      return (generator.validWalkCord(x, y) && world[generator.getIndex(x, y)] == Terrain.AIR);
   }

   private boolean is_lava(int x, int y){
      return (generator.validWalkCord(x, y) && tile_attributes[generator.getIndex(x, y)] == Terrain.LAVA);
   }


   private boolean is_door(int x, int y){
      return (generator.validWalkCord(x, y) && (
              tile_attributes[generator.getIndex(x, y)] == Terrain.DOOR_BODY ||
              tile_attributes[generator.getIndex(x, y)] == Terrain.BASIC_DOOR_START ||
              tile_attributes[generator.getIndex(x, y)] == Terrain.KILL_DOOR_START
      ));
   }

   private boolean is_hookable(int x, int y){
      return (generator.validWalkCord(x, y) && tile_attributes[generator.getIndex(x, y)] != Terrain.NON_GRAPPLE);
   }


   public static PVector random_hsl_colour() {
      int h = new Random().nextInt(360);
      return hsl_colour(h, 1f, 0.6f);
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


   /* ******** Helper Classes ********* */
   private static class Air {
      public float x;
      public float y;
      public float width;

      public Air(float x, float y, float width) {
         this.x = x;
         this.y = y;
         this.width = width;
      }
   }

   private static class Lava {
      public float x;
      public float y;
      public float width;
      public boolean is_top;
      public PGif gif;

      public Lava(float x, float y, float width, boolean is_top, PGif root) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.is_top = is_top;
         this.gif = root.copy();
      }
   }

   private static class Wall {
      public float x;
      public float y;
      public float width;

      public Wall(float x, float y, float width) {
         this.x = x;
         this.y = y;
         this.width = width;
      }
   }

   private static class RectMask {
      public float x;
      public float y;
      public float height;
      public float width;

      public RectMask(float x, float y, float height, float width) {
         this.x = x;
         this.y = y;
         this.height = height;
         this.width = width;
      }
   }

   private static class EdgeMask {
      public float x;
      public float y;
      public float width;
      public float height;
      public float start_rad;
      public float end_rad;

      public EdgeMask(float x, float y, float width, float height, float start_rad, float end_rad) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.start_rad = start_rad;
         this.end_rad = end_rad;
      }
   }
}

package GameEngine.Components.TerrianComponents;

import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Terrain;
import processing.core.PVector;

import java.util.*;
import java.util.stream.Collectors;

public class AdvancedTerrainGenerator extends TerrainGenerator {
   // Attributes
   public static final boolean DISPLAY_REGIONS  = false;
   public static final boolean ENSURE_ROOMS     = false;
   public static final float MIN_SPACE          = 0.55f;

   private static int r_mask = 255;
   private static int g_mask = 65280;
   private static int b_mask = 16711680;

   private int[] world;
   private Random random;

   // Constructor
   public AdvancedTerrainGenerator(GameObject parent, int seed) {
      super(parent, seed);
      random = new Random(seed);
   }

   // Methods
   @Override
   public int[] createWorld() {
      // Init world object
      world = new int[width * height];
      Arrays.fill(world, Terrain.WALL);

      // Generate Initial world
      //perlin_generation();
      cellular_generation();

      // Split the world into regions
      List<List<PVector>> regions = create_regions();

      // Fill all small regions
      regions = fill_small_regions(regions, 10);

      // Check the world has enough space
      if(spaceArea(world) < MIN_SPACE)
         return createWorld();

      // Check there are enough rooms.
      if(regions.size() < 2)
         return createWorld();

      // Debug draw the regions
      display_regions(regions);
      // Ensure connectivity between all regions
      connect_regions(regions);

      // Double check regions are all connected
      if(create_regions().size() != 1)
         return createWorld();
      return world;
   }

   @Override
   public int[] getWorld() {
      if(world == null)
         return createWorld();
      return world;
   }

   /* ******** Region Connecting algorithms ******** */
   private void connect_regions(List<List<PVector>> regions){
      // Calculate the median point of each region
      ArrayList<Region> region_data = get_region_data(regions);

      // Create a graph where nodes are regions and vertexes are
      // the distance between those regions.
      ArrayList<RegionEdge> edges = create_map(region_data);

      // Create minimum spanning tree
      ArrayList<RegionEdge> tree = new ArrayList<>();
      edges.sort(RegionEdge.comparingWeight().reversed());

      // Build Tree
      int i = 0;

      while (tree.size() < regions.size() - 1){
         // Pick smallest
         RegionEdge edge = edges.get(i++);

         // Check this edge does not create a cycle
         HashSet<RegionEdge> visited = new HashSet<>();
         tree.add(edge);

         if(creates_cycle(edge.region1, edge.region1, tree, visited))
            tree.remove(edge);
      }

      // Connect all regions
      for(var edge : tree)
         connect_edge(edge);
   }


   private void connect_edge(RegionEdge edge){
      // Init start and end points
      PVector start = edge.region1_start_point.copy().add((float)Terrain.CELL_SIZE/2, (float)Terrain.CELL_SIZE/2);
      PVector end = edge.region2_start_point.copy().add((float)Terrain.CELL_SIZE/2, (float)Terrain.CELL_SIZE/2);

      // Create a line of space between these two edges
      PVector ray_start = start;
      PVector ray_direction = PVector.sub(end, ray_start).normalize();
      PVector ray_unit_step_size = new PVector(
              (float)Math.sqrt((float)1 + Math.pow(ray_direction.y / ray_direction.x, 2)),
              (float)Math.sqrt((float)1 + Math.pow(ray_direction.x / ray_direction.y, 2))
      );
      PVector current_cell = new PVector((int)(ray_start.x / Terrain.CELL_SIZE), (int)(ray_start.y / Terrain.CELL_SIZE));
      PVector ray_length_1d = new PVector(0, 0);
      PVector step = new PVector(0, 0);

      if(ray_direction.x < 0){ // Determine what step size to use on the x-axis
         step.x = -1; // Step Left
         ray_length_1d.x = (ray_start.x - current_cell.x) * ray_unit_step_size.x; // How much left in cell to left
      } else {
         step.x = 1; // Step Right
         ray_length_1d.x = ((current_cell.x + 1) - ray_start.x) * ray_unit_step_size.x; // How much left in cell to right
      }

      if(ray_direction.y < 0){ // Determine what step size to use on the y-axis
         step.y = -1; // Step Down
         ray_length_1d.y = (ray_start.y - current_cell.y) * ray_unit_step_size.y; // How much left in cell below
      } else {
         step.y = 1; // Step Up
         ray_length_1d.y = ((current_cell.y + 1) - ray_start.y) * ray_unit_step_size.y; // How much left in cell above
      }

      world[getIndex((int)current_cell.x, (int)current_cell.y)] = Terrain.AIR;
      while((int)current_cell.x != (int)end.x || (int)current_cell.y != (int)end.y) {
         if (ray_length_1d.x < ray_length_1d.y) {
            // Walk In X Direction
            current_cell.x += step.x;
            ray_length_1d.x += ray_unit_step_size.x;
         } else {
            // Walk in Y Direction
            current_cell.y += step.y;
            ray_length_1d.y += ray_unit_step_size.y;
         }

         // Make current cell empty
         if(current_cell.x < 0 || current_cell.y < 0 || current_cell.y >= Terrain.HEIGHT || current_cell.x >= Terrain.WIDTH)
            break;
         world[getIndex((int)current_cell.x, (int)current_cell.y)] = Terrain.AIR;
      }
   }

   private boolean creates_cycle(Region end, Region current, ArrayList<RegionEdge> edges, HashSet<RegionEdge> visited){
      // Check all edges from current
      for(var edge : edges){
         // Check if this is an edge for the current
         Region next;

         if(edge.region1 == current){
            next = edge.region2;
         } else if (edge.region2 == current){
            next = edge.region1;
         } else {
            continue;
         }

         // Is an edge check not already visited
         if(visited.contains(edge))
            continue; // Already visited

         // Check if next creates a cycle
         if(next == end)
            return false; // Cycle created

         // Check if there is a cycle created somewhere else
         visited.add(edge);

         if(creates_cycle(end, next, edges, visited))
            return false;

         visited.remove(edge);
      }

      return false;
   }

   private ArrayList<RegionEdge> create_map(ArrayList<Region> regions){
      // Create a graph where all regions are connected
      ArrayList<RegionEdge> graph = new ArrayList<>();

      // Add each region to the graph
      for(int i = 0; i < regions.size(); i++){
         for(int j = i; j < regions.size(); j++){
            // Get the two regions
            Region r1 = regions.get(i);
            Region r2 = regions.get(j);

            // Find two closet points from these two regions
            PVector[] closest_points = closets_two_points(r1.points, r2.points);
            PVector r1p = closest_points[0];
            PVector r2p = closest_points[1];

            // Create an edge between these two regions
            graph.add(new RegionEdge(
               PVector.dist(r1p, r2p),
               r1,
               r1p.copy(),
               r2,
               r2p.copy()
            ));
         }
      }

      return graph;
   }

   private ArrayList<Region> get_region_data(List<List<PVector>> regions){
      // Init output
      ArrayList<Region> out = new ArrayList<>(regions.size());

      for(var region : regions){
         Region region_data = new Region(region, new PVector(0, 0));

         region_data.mid = (region
               .stream()
               .reduce(new PVector(0, 0), (average, point) ->
                 average.add(point.x / region.size(), point.y / region.size())
               )
         );

         out.add(region_data);
      }

      return out;
   }

   private PVector[] closets_two_points(List<PVector> l1, List<PVector> l2){
      // Init output
      PVector[] out = new PVector[2];

      // Find closest two points
      PVector l1_min = null;
      PVector l2_min = null;
      float dist = 100000f;

      for(var p1 : l1){
         // For this point find the closest
         PVector p1_min = null;
         float p1_dist = 100000f;

         for(var p2 : l2){
            float d = PVector.dist(p2, p1);
            if(d < p1_dist){
               p1_min = p2;
               p1_dist = d;
            }
         }

         if(p1_dist < dist){
            l1_min = p1;
            l2_min = p1_min;
            dist = p1_dist;
         }
      }

      out[0] = l1_min;
      out[1] = l2_min;

      return out;
   }



   /* ******** Region Filling algorithms ******** */
   private List<List<PVector>> create_regions(){
      // Init regions list
      List<List<PVector>> regions = new ArrayList<>();
      boolean[] visited = new boolean[world.length];
      Arrays.fill(visited, false);

      // Find all regions
      int[][] offsets = new int[][] {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

      for(int x = 0; x < width; x++){
         for(int y = 0; y < height; y++){
            // Get index for this pos
            int i = getIndex(x, y);

            // Check if already visited this space
            if(world[i] == Terrain.WALL || visited[i])
               continue;

            // Create region for this node
            ArrayList<PVector> region = new ArrayList<>();
            LinkedList<PVector> to_visit = new LinkedList<>();
            to_visit.add(new PVector(x, y));

            while(!to_visit.isEmpty()){
               // Get node to visit
               PVector pos = to_visit.pop();

               // Check this is a valid position;
               if(!isValidPosition((int)pos.x, (int)pos.y))
                  continue;

               // check not visited before
               int index = getIndex((int)pos.x, (int)pos.y);
               if(visited[index] || world[index] == Terrain.WALL)
                  continue;

               // Valid index add to region
               region.add(pos.copy());

               // Get all touching points
               visited[index] = true;

               to_visit.addAll(Arrays
                  .stream(offsets)
                  .map(offset -> PVector.add(pos, new PVector(offset[0], offset[1])))
                  .collect(Collectors.toList()));
            }

            // Add region to regions list
            regions.add(region);
         }
      }


      // Finished
      return regions;
   }


   private void display_regions(List<List<PVector>> regions){
      if(DISPLAY_REGIONS){
         // Display each region as a colour
         for(var region : regions){
            // Generate a colour for this region
            int colour = random_colour();


            // Set these pixels to that region
            for(var i : region){
               world[getIndex((int)i.x, (int)i.y)] = colour;
            }
         }
      }
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


   private List<List<PVector>> fill_small_regions(List<List<PVector>> regions, int min_area){
      // Remove any regions which are too small and return the updated regions list
      List<List<PVector>> new_regions = new LinkedList<>();

      // Add all regions which are big enough
      for(var region : regions){
         if(region.size() >= min_area)
            // Region is big enough keep
            new_regions.add(region);
         else
            // Region is too small. Fill that area
            region.forEach(index -> world[getIndex((int)index.x, (int)index.y)] = Terrain.WALL);
      }

      // Finished
      return new_regions;
   }


   /* ******** Initial World Generation Algorithms ********* */
   private void perlin_generation(){
      // Procedurally generate caves
      if(seed != -1)
         sys.noiseSeed(seed);

      float air_threshold = 0.5f;
      float scale = 0.3f; // Smaller = bigger caves

      for(int x = 1; x < width - 1; x++){
         for(int y = 1; y < height - 1; y++){
            if(noise(x, y, scale) > air_threshold)
               world[getIndex(x, y)] = Terrain.AIR;
         }
      }
   }


   private void cellular_generation(){
      // Constants
      float space_rate = MIN_SPACE + 0.05f;
      int num_iterations = 7;
      int cut_off = num_iterations - 3;

      // Inti random world
      for(int i = 0; i < world.length; i++)
         world[i] = random.nextFloat() > space_rate ? Terrain.WALL : Terrain.AIR;

      // Use cellular automata to generate caves from the random world
      // Adapted from http://www.roguebasin.com/index.php?title=Cellular_Automata_Method_for_Generating_Random_Cave-Like_Levels
      int[][] iterations = new int[num_iterations + 1][world.length];
      iterations[0] = world;

      if(!ENSURE_ROOMS)
         addWalls(iterations[0]);

      for (int i = 1; i < num_iterations + 1; i++) {
         // Add walls to the world to ensure differnet rooms
         if(ENSURE_ROOMS)
            addWalls(iterations[i - 1]);

         // Update the world
         for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
               // A tile becomes a wall if
               //    it was a wall and 4 or more of its eight neighbors were walls,
               //    or if it was not a wall and 5 or more of its neighbors were

               int num_walls1 = wallCount1(x, y, iterations[i - 1]);
               int num_walls2 = wallCount2(x, y, iterations[i - 1]) + num_walls1;

               // Update the current cell
               int w1 = iterations[i - 1][getIndex(x, y)] == Terrain.WALL ?
                       num_walls1 >= 4 ? Terrain.WALL : Terrain.AIR :
                       num_walls1 >= 5 ? Terrain.WALL : Terrain.AIR;
               int w2 = iterations[i - 1][getIndex(x, y)] == Terrain.WALL ?
                       num_walls2 <= 1 ? Terrain.WALL : Terrain.AIR :
                       num_walls2 <= 2 ? Terrain.WALL : Terrain.AIR;

               if(i > cut_off)
                  iterations[i][getIndex(x, y)] = w1;
               else
                  iterations[i][getIndex(x, y)] = (w1 == Terrain.WALL || w2 == Terrain.WALL) ? Terrain.WALL : Terrain.AIR;
            }
         }
      }

      // Set world and ensure there is a complete wall around it
      world = iterations[num_iterations];

      for(int x = 0; x < width; x++) {
         world[getIndex(x, 0)] = Terrain.WALL;
         world[getIndex(x, height - 1)] = Terrain.WALL;
      }
      for(int y = 0; y < height; y++) {
         world[getIndex(0, y)] = Terrain.WALL;
         world[getIndex(width - 1, y)] = Terrain.WALL;
      }
   }


   private void addWalls(int[] world){
      // Add Walls to mid lines
      for(int x = 0; x < width; x++)
         world[getIndex(x, height / 2)] = Terrain.WALL;
      for(int y = 0; y < height; y++)
         world[getIndex(width / 2, y)] = Terrain.WALL;
   }


   private float spaceArea(int[] world){
      return 1f - ((float) Arrays.stream(world).map(cell -> cell == Terrain.WALL ? 1 : 0).sum() / world.length);
   }


   private float noise(float x, float y, float scale){
      return sys.noise(x * scale, y * scale);
   }


   private boolean isValidPosition(int x, int y){
      return x >= 0 && y >= 0 && x < width && y < height;
   }


   private int wallCount1(int x, int y, int[] world){
      // Init offsets
      int[][] offsets = new int[][] {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

      // count the number of walls around this position
      return wallCount(x, y, world, offsets);
   }


   private int wallCount2(int x, int y, int[] world){
      // Init offsets
      int[][] offsets = new int[][] {
              {0, 2}, {0, -2}, {2, 0}, {-2, 0}, {2, 2}, {-2, -2}, {2, -2}, {-2, 2},
              {-1, -2}, {-2, -1}, {2, 1}, {1, 2}, {3, 3}, {-3, -3}, {3, -3}, {-3, 3}
      };

      // count the number of walls around this position
      return wallCount(x, y, world, offsets);
   }


   private int wallCount(int x, int y, int[] world, int[][] offsets){
      return Arrays.stream(offsets).mapToInt(offset -> {
         // Get new position
         int new_x = x + offset[0];
         int new_y = y + offset[1];

         if (isValidPosition(new_x, new_y))
            return world[getIndex(new_x, new_y)] == Terrain.WALL ? 1 : 0;
         return 1;
      }).sum();
   }


   /* ********* Region Graph Code ********* */
   private static class RegionEdge {
      public float weight;
      public Region region1;
      public PVector region1_start_point;
      public Region region2;
      public PVector region2_start_point;

      public RegionEdge(float weight, Region region1, PVector region1_start_point, Region region2, PVector region2_start_point) {
         this.weight = weight;
         this.region1 = region1;
         this.region1_start_point = region1_start_point;
         this.region2 = region2;
         this.region2_start_point = region2_start_point;
      }

      public static Comparator<RegionEdge> comparingWeight(){
         return new Comparator<RegionEdge>() {
            @Override
            public int compare(RegionEdge o1, RegionEdge o2) {
               return Float.compare(o1.weight, o2.weight);
            }
         };
      }
   }

   private static class Region {
      public List<PVector> points;
      public PVector mid;

      public Region(List<PVector> points, PVector mid) {
         this.points = points;
         this.mid = mid;
      }
   }
}

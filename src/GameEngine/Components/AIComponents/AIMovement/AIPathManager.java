package GameEngine.Components.AIComponents.AIMovement;


import GameEngine.Components.Component;
import GameEngine.Components.TerrianComponents.TerrainGenerator;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.Core.Terrain;
import processing.core.PVector;

import java.util.*;


public class AIPathManager extends Component {
   // Attributes
   private final ArrayList<Node> nodes;
   private final HashMap<Integer, Node> index_to_nodes;
   private TerrainGenerator generator;

   public PVector player_ground_tile;
   public Player player;

   // Constructor
   public AIPathManager(GameObject parent) {
      super(parent);

      // Init attributes
      this.nodes = new ArrayList<>();
      this.index_to_nodes = new HashMap<>();
      this.player_ground_tile = new PVector();
   }


   // Methods 
   public void start() {
       init_walkable_areas();

       player = sys.getGameObject(Player.class);
   }

   public void update() {
      update_player_ground_tile();
   }


   public void draw() {
      for(Node node : nodes){
         sys.fill(0, 255, 0, 127);
         sys.rect(node.pos.x, node.pos.y, Terrain.CELL_SIZE, Terrain.CELL_SIZE);
         sys.fill(255, 0, 0);
         sys.circle(node.centre_pos.x, node.centre_pos.y, 0.1f);

         for(Edge edge : node.adjacent){
            if(edge instanceof VerticalEdge){
               VerticalEdge jedge = (VerticalEdge) edge;

               sys.stroke(255, 0, 0);
               sys.line(node.centre_pos.x, node.centre_pos.y, jedge.upper_pos.x, jedge.upper_pos.y);
               sys.line(jedge.upper_pos.x, jedge.upper_pos.y, jedge.node.centre_pos.x, jedge.node.centre_pos.y);
               sys.noStroke();
            } else {
               sys.stroke(0, 0, 255);
               sys.line(node.centre_pos.x, node.centre_pos.y, edge.node.centre_pos.x, edge.node.centre_pos.y);
               sys.noStroke();
            }
         }
      }
      sys.fill(250, 215, 30);
      sys.rect(player_ground_tile.x, player_ground_tile.y, Terrain.CELL_SIZE, Terrain.CELL_SIZE);
   }


   // ******** Tracking Methods ********* //
   private void update_player_ground_tile() {
      // Get the ground position of the player
      float x = player.pos.x + Player.COLLISION_WIDTH / 2f;
      float y = player.pos.y;
      int index;

      do {
         index = generator.getIndexFromWorldPos(x, y--);
      }while(!index_to_nodes.containsKey(index));

      Node node = index_to_nodes.get(index);
      player_ground_tile.x = node.pos.x;
      player_ground_tile.y = node.pos.y;
   }


   // ********* Graph Generation ********* //
   private void init_walkable_areas(){
      // Loop over each point on the world
      generator = sys.terrain.getComponent(TerrainGenerator.class);
      int[] world = generator.getWorld();

      // Create graph
      create_nodes(world);
      create_edges(world);
      create_vertical_edges(world);
   }

   private void create_nodes(int[] world){
      // Loop over each point in the map
      for(int x = 0; x < generator.getWidth(); x++)
         for(int y = 0; y < generator.getHeight(); y++){
            // Get index of this position
            int index = generator.getIndex(x, y);

            if(is_walkable(world, x, y, index)) {
               Node node = new Node(new PVector(x, y), index);

               nodes.add(node);
               index_to_nodes.put(index, node);
            }
         }
   }


   private void create_edges(int[] world) {
      // Connect all nodes, in which a monster could jump/fall to them
      for (Node node : nodes) {
         // Add any edges to node
         for (int x_offset : new int[]{-1, 1}) {
            // Get pos of possible adjacent node
            int x = (int) node.pos.x + x_offset;
            int y = (int) node.pos.y;

            if (!is_valid_and_walkable(world, x, y))
               continue;

            // Found an adjacent node
            int index = generator.getIndex(x, y);
            Node adj_node = index_to_nodes.get(index);

            // Create and add edge
            Edge edge = new Edge(adj_node, (float) (Math.pow(node.pos.x - x, 2) + Math.pow(node.pos.y - y, 2)));
            node.adjacent.add(edge);
         }
      }
   }


   private void create_vertical_edges(int[] world) {
      // Connect all adjacent nodes
      for (Node node : nodes) {
         // Check if there is a position to jump to above
         int x = (int) node.pos.x;
         int y = (int) node.pos.y + 1;

         while (valid_index(x, y) && world[generator.getIndex(x, y)] == Terrain.AIR) {
            for (int x_offset : new int[]{-1, 1}) {
               // get possible node index
               int node_x = x + x_offset;
               int node_index = generator.getIndex(node_x, y);

               // Check if node is walkable
               if(!is_valid_and_walkable(world, node_x, y))
                  continue;

               // Add Vertical edge to both nodes
               PVector jump_pos = new PVector(node_x - x_offset + Terrain.CELL_SIZE / 2f, y + Terrain.CELL_SIZE / 2f);
               Node other_node = index_to_nodes.get(node_index);
               float weight = (float) (Math.pow(node.pos.x - jump_pos.x, 2) + Math.pow(node.pos.y - jump_pos.y, 2));

               node.adjacent.add(new VerticalEdge(other_node, jump_pos, weight, false));
               other_node.adjacent.add(new VerticalEdge(node, jump_pos, weight, true));
            }

            // Move to next possible node
            y += 1;
         }
      }
   }


   private boolean is_walkable(int[] world, int x, int y, int index){
      return world[index] == Terrain.AIR && y > 0 && world[generator.getIndex(x, y - 1)] == Terrain.WALL;
   }

   private boolean valid_index(int x, int y){
      return x >= 0 && x <= generator.getWidth() && y >= 0 && y <= generator.getHeight();
   }


   private boolean is_valid_and_walkable(int[] world, int x, int y){
      return x >= 0 && x < generator.getWidth() && y > 0 && y < generator.getHeight() &&
             world[generator.getIndex(x, y)] == Terrain.AIR && world[generator.getIndex(x, y - 1)] == Terrain.WALL;
   }

   // ******** Graph Search ********* //
   public Path astar_search(PVector start, PVector end){
      // Adapted from: // https://stackabuse.com/graphs-in-java-a-star-algorithm/
      // First reset eval metrics on all nodes
      //nodes.forEach(Node::resetMetrics); // Todo: still not sure if this is okay to do

      // Get the nodes the start and end belong to
      Node start_node = index_to_nodes.get(generator.getIndexFromWorldPos(start.x, start.y));
      Node end_node = index_to_nodes.get(generator.getIndexFromWorldPos(end.x, end.y));

      if(start_node == end_node){
         Path p = new Path();
         p.points.add(new Path.Point(start.copy()));
         p.points.add(new Path.Point(end.copy()));
         System.out.println("Same");
         return p;
      }

      // Perform a* search to create a path between the start and end nodes
      // Init data structures required to perform the search
      PriorityQueue<Node> closed_list = new PriorityQueue<>();
      PriorityQueue<Node> open_list = new PriorityQueue<>();

      start_node.f = start_node.g + start_node.getHeuristicScore(end_node);
      open_list.add(start_node);

      // Perform the search
      while(!open_list.isEmpty()){
         // Get next closest node
         Node n = open_list.peek();
         if(n == end_node){
            // Found path to end
            break;
         }

         // Look at all it's connecting condes
         for(Edge edge : n.adjacent){
            Node m = edge.node;
            float weight = n.g + edge.weight;

            if (!open_list.contains(m) && !closed_list.contains(m)) {
               m.h_target = null;
               m.parent = n;
               m.connection_edge = edge;
               m.g = weight;
               m.f = m.g + m.getHeuristicScore(end_node);

               open_list.add(m);
            }else if(weight < m.g){
               m.parent = n;
               m.connection_edge = edge;
               m.g = weight;
               m.f = m.g + m.getHeuristicScore(end_node);

               if(closed_list.contains(m)){
                  closed_list.remove(m);
                  open_list.add(m);
               }
            }
         }

         open_list.remove(n);
         closed_list.add(n);
      }

      // Found path to parent convert this to a usable search path
      LinkedList<Path.Point> path = new LinkedList<>();
      path.addFirst(new Path.Point(end.copy()));

      Node n = end_node.parent;
      boolean last_was_jump = false;
      while(n != start_node && n != null){
         if(n.connection_edge instanceof VerticalEdge) {
            path.addFirst(new Path.Point(n.centre_pos, true, ((VerticalEdge) n.connection_edge).is_upper));
            last_was_jump = true;
         }else if (last_was_jump) {
            path.addFirst(new Path.Point(n.centre_pos, true, !path.getFirst().is_bottom));
            last_was_jump = false;
         }else{
            path.addFirst(new Path.Point(n.centre_pos));
         }
         n = n.parent;
      }

      path.addFirst(new Path.Point(start.copy())); // Todo: deal with start being a jedge

      if(n == null) // Could not create a path to the player
         path.removeLast();

      // Create output path
      Path out = new Path();
      out.points.addAll(path);

      return out;
   }


   // ********* Graph Representation ********* //
   private static class Node implements Comparable<Node>{
      // Attributes
      public final PVector centre_pos;
      public final PVector pos;
      public final int index;
      public final ArrayList<Edge> adjacent;

      // Evaluation metrics
      public float f;
      public float g;
      private float h;
      private Node h_target;
      public Node parent;
      public Edge connection_edge;


      // Constructor
      public Node(PVector pos, int index) {
         this.centre_pos = PVector.add(pos, new PVector(Terrain.CELL_SIZE / 2f, Terrain.CELL_SIZE / 2f));
         this.pos = pos;
         this.index = index;
         this.adjacent = new ArrayList<>();
         this.parent = null;
         this.connection_edge = null;
      }


      // Methods
      @Override
      public int compareTo(Node n) {
         return Float.compare(f, n.f);
      }


      public void resetMetrics(){
         f = 1000000;
         g = 1000000;
         parent = null;
      }


      public float getHeuristicScore(Node target){
         if(target == h_target)
            return h; // Already calculated h

         // Calculate h
         float x_diff = target.pos.x - pos.x;
         float y_diff = target.pos.y - pos.y;
         h = x_diff * x_diff + y_diff * y_diff;
         h_target = target;

         return h;
      }
   }

   private static class Edge {
      // Attributes
      public final Node node;
      public final float weight;

      // Constructor
      public Edge(Node node, float weight) {
         this.node = node;
         this.weight = weight;
      }
   }


   private static class VerticalEdge extends Edge{
      // Attributes
      public final PVector upper_pos;
      public final boolean is_upper;

      // Constructor
      public VerticalEdge(Node node, PVector upper_pos, float weight, boolean is_upper) {
         super(node, weight);
         this.upper_pos = upper_pos;
         this.is_upper = is_upper;
      }
   }

}

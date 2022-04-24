package GameEngine.Components.TerrianComponents;


import GameEngine.Components.AIComponents.AIMovementController.Path;
import GameEngine.Components.Component;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Terrain;
import processing.core.PVector;

import java.util.*;


public class AIPathManager extends Component {
   // Attributes
   private final ArrayList<Node> nodes;
   private final HashMap<Integer, Node> index_to_nodes;
   private TerrainGenerator generator;


   // Constructor
   public AIPathManager(GameObject parent) {
      super(parent);

      // Init attributes
      this.nodes = new ArrayList<>();
      this.index_to_nodes = new HashMap<>();
   }


   // Methods 
   public void start() {
       init_walkable_areas();
   }

   public void update() {
   }


   public void draw() {
      for(Node node : nodes){
         sys.fill(0, 255, 0);
         sys.rect(node.pos.x, node.pos.y, Terrain.CELL_SIZE, Terrain.CELL_SIZE);
         sys.fill(255, 0, 0);
         sys.circle(node.centre_pos.x, node.centre_pos.y, 0.1f);

         for(Edge edge : node.adjacent){
            sys.stroke(0, 0, 255);
            sys.line(node.centre_pos.x, node.centre_pos.y, edge.node.centre_pos.x, edge.node.centre_pos.y);
            sys.noStroke();
         }
      }
   }


   // ********* Graph Generation ********* //
   private void init_walkable_areas(){
      // Loop over each point on the world
      generator = parent.getComponent(TerrainGenerator.class);
      int[] world = generator.getWorld();

      // Create Nodes
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

      // Create edges between nodes
      for(Node node : nodes){
         // Add any edges to node
         for(int x_offset : new int[] {-1, 1}) {
            // Get pos of possible adjacent node
            int x = (int)node.pos.x + x_offset;
            int y = (int)node.pos.y;

            if(!is_valid_and_walkable(world, x, y))
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


   private boolean is_walkable(int[] world, int x, int y, int index){
      return world[index] == Terrain.AIR && y > 0 && world[generator.getIndex(x, y - 1)] == Terrain.WALL;
   }


   private boolean is_valid_and_walkable(int[] world, int x, int y){
      return x >= 0 && x < generator.getWidth() && y > 0 && y < generator.getHeight() &&
             world[generator.getIndex(x, y)] == Terrain.AIR && world[generator.getIndex(x, y - 1)] == Terrain.WALL;
   }

   // ******** Graph Search ********* //
   public Path search(PVector start, PVector end){
      // Adapted from: // https://stackabuse.com/graphs-in-java-a-star-algorithm/
      // First reset eval metrics on all nodes
      nodes.forEach(Node::resetMetrics);

      // Get the nodes the start and end belong to
      Node start_node = index_to_nodes.get(generator.getIndexFromWorldPos(start.x, start.y));
      Node end_node = index_to_nodes.get(generator.getIndexFromWorldPos(end.x, end.y));

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
               m.parent = n;
               m.g = weight;
               m.f = m.g + m.getHeuristicScore(end_node);

               open_list.add(m);
            }else if(weight < m.g){
               m.parent = n;
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
      path.addFirst(new Path.Point(end));

      Node n = end_node.parent;
      while(n != start_node){
         path.addFirst(new Path.Point(n.centre_pos));
         n = n.parent;
      }

      path.addFirst(new Path.Point(start));

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


      // Constructor
      public Node(PVector pos, int index) {
         this.centre_pos = PVector.add(pos, new PVector(Terrain.CELL_SIZE / 2f, Terrain.CELL_SIZE / 2f));
         this.pos = pos;
         this.index = index;
         this.adjacent = new ArrayList<>();
         this.parent = null;
      }


      // Methods
      @Override
      public int compareTo(Node n) {
         return Float.compare(f, n.f);
      }


      public void resetMetrics(){
         f = Float.MAX_VALUE - 10000;
         g = Float.MAX_VALUE - 10000;
         parent = null;
      }


      public float getHeuristicScore(Node target){
         if(target == h_target)
            return h; // Already calculated h

         // Calculate h
         h = (float) (Math.pow(target.pos.x - pos.x, 2) + Math.pow(target.pos.y - pos.y, 2));
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

//   private static class JumpEdge extends Edge{
//    // Todo: implement
//   }

//   private static class DropEdge extends Edge {
//    // Todo: implement
//   }
}

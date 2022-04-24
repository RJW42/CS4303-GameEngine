package GameEngine.Components.AIComponents.AIMovementController;

import processing.core.PVector;

import java.util.ArrayList;

public class Path {
   // Attributes
   public int current_index;
   public ArrayList<Point> points;

   // Constructor
   public Path() {
      this.current_index = 0;
      this.points = new ArrayList<>();
   }

   public Point getNextPoint(){
      return points.get(++current_index);
   }

   public Point getCurrentPoint(){
      return points.get(current_index);
   }

   // Methods
   public static class Point {
      // Attributes
      public final PVector pos;

      // Constructor
      public Point(PVector pos) {
         this.pos = pos;
      }

      // Methods
   }
}

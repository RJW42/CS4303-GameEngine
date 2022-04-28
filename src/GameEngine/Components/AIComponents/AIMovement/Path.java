package GameEngine.Components.AIComponents.AIMovement;

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
      if(current_index == points.size() - 1)
         return null;
      return points.get(++current_index);
   }

   public Point peekNextPoint(){
      if(current_index == points.size() - 1)
         return null;
      return points.get(current_index + 1);
   }

   public Point getCurrentPoint(){
      return points.get(current_index);
   }

   // Methods
   public static class Point {
      // Attributes
      public final PVector pos;
      public final PVector upper_pos;
      public boolean is_jump;
      public boolean is_bottom;

      // Constructor
      public Point(PVector pos) {
         this.pos = pos;
         this.is_jump = false;
         this.is_bottom = false;
         this.upper_pos = null;
      }

      public Point(PVector pos, boolean is_jump, boolean is_bottom) {
         this.pos = pos;
         this.is_jump = is_jump;
         this.is_bottom = is_bottom;
         this.upper_pos = null;
      }

      public Point(PVector pos, PVector upper_pos, boolean is_jump, boolean is_bottom) {
         this.pos = pos;
         this.upper_pos = upper_pos;
         this.is_jump = is_jump;
         this.is_bottom = is_bottom;
      }

      // Methods
   }
}

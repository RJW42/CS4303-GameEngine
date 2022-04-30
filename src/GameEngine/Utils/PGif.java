package GameEngine.Utils;

import GameEngine.GameEngine;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.util.Arrays;

public class PGif {
   // Attributes
   public final int width;
   public final int height;
   public PImage[] frames;
   public int fps;
   public boolean finished;

   private float time_per_frame;
   private float time_since_start;
   private boolean loop;


   // Constructor
   public PGif(PImage[] frames){
      // Init attributes
      this(frames, frames.length, true);
   }

   public PGif(PImage[] frames, int fps, boolean loop){
      // Init attributes
      this.width = frames[0].width;
      this.height = frames[0].height;
      this.frames = frames;
      this.loop = loop;
      this.finished = false;
      setFPS(fps);
   }


   // Methods
   public void setFPS(int fps){
      this.fps = fps;
      this.time_since_start = 0;
      this.time_per_frame = 1f / fps;
   }


   public void setLooping(boolean loop){
      this.loop = loop;
      this.finished = false;
   }


   public void restart(){
      this.finished = false;
      this.time_since_start = 0;
   }


   public void resize(int w, int h){
      Arrays.stream(frames).forEach(frame -> frame.resize(w, h));
   }


   public PGif copy(){
      // Copy each frame
      PImage[] frames = new PImage[this.frames.length];
      for(int i = 0; i < frames.length; i++)
         frames[i] = this.frames[i].copy();

      return new PGif(frames, fps, loop);
   }

   public void play(GameEngine sys, PVector pos, float rotation_angle) {
      // Check if can play
      if(finished)
         return;

      // Check which frame to play
      int frame_number = (int)(time_since_start / time_per_frame);

      if(frame_number >= frames.length){
         frame_number = 0;
         time_since_start = 0;

         if(!loop){
            // Don't loop so stop playing
            finished = true;
         }
      }

      // Display frame
      sys.image(frames[frame_number], pos.x, pos.y, rotation_angle);

      // Update time
      time_since_start += sys.DELTA_TIME;
   }
}

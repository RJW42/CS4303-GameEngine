package GameEngine;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class InputManager {
   // Attributes
   private final GameEngine sys;
   public boolean[] keys_pressed;
   public boolean[] mouse_pressed;
   public KeyEvent latest_key;

   // Constructor
   public InputManager(GameEngine sys) {
      this.sys = sys;
      this.keys_pressed = new boolean[512];
      this.mouse_pressed = new boolean[256];
   }

   // Methods
   // Todo: add methods for getting if button pressed based off name
   public void reset(){
      // Todo: load defaults
   }

   public void setMapping(){
      // Todo: take name change button
   }

   /* ***** Processing Functions ***** */
   public void keyPressed(KeyEvent event) {
      keys_pressed[event.getKeyCode() % 512] = true;
      latest_key = event;
      System.out.println(event.getKeyCode());
   }

   public void keyReleased(KeyEvent event) {
      keys_pressed[event.getKeyCode() % 512] = false;
   }

   public void mousePressed(MouseEvent event) {
      mouse_pressed[event.getButton() % 256] = true;
      System.out.println(event.getButton());
   }

   public void mouseReleased(MouseEvent event) {
      mouse_pressed[event.getButton() % 256] = false;
   }
}

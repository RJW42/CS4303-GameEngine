package GameEngine.Utils.Managers;

import GameEngine.GameEngine;
import GameEngine.Triggers.InputComplete;
import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class InputManager {
   // Attributes
   private final GameEngine sys;
   public boolean[] keys_pressed;
   public boolean[] mouse_pressed;
   public KeyEvent latest_key;
   public String current_string;

   private Key[] tracked_keyboard_keys;
   private Key[] tracker_mouse_keys;
   private InputComplete current_callback;
   private StringBuilder current_string_builder;

   // Constructor
   public InputManager(GameEngine sys) {
      this.sys = sys;
      this.keys_pressed = new boolean[512];
      this.mouse_pressed = new boolean[256];
      this.tracked_keyboard_keys = new Key[512];
      this.tracker_mouse_keys = new Key[512];
   }

   // Methods

   /**
    * Returns a reference to a key given its name
    *
    * The key will contain a true or false value for if
    * it is pressed. This is updated automatically by the input
    * manager.
    *
    * An example name could be up, down, fire, ect...
    * @param name id of the key being pressed
    * @return reference to that key
    */
   public Key getKey(String name){
      // Todo: implement
      return null;
   }


   public void init(){
      // Todo: load defaults
   }


   public void setMapping(){
      // Todo: take name change button
   }


   public boolean getInput(InputComplete callback){
      // Check if, can track input
      if(current_callback != null)
         return false; // Can's start tracking an input as already doing so

      // Start tracking input from keyboard
      current_callback = callback;
      current_string = "";
      current_string_builder = new StringBuilder();

      return true;
   }


   private void handle_callback(){
      // Check if the current key is an escape/enter
      if(latest_key.getKey() == PConstants.ENTER){
         current_callback.callback(current_string_builder.toString(), false);
         finish_callback();
         return;
      }

      if(latest_key.getKey() == PConstants.ESC){
         current_callback.callback(current_string_builder.toString(), true);
         finish_callback();
         return;
      }

      // Not an escape/entre so keep building the string
      if(latest_key.getKey() == PConstants.BACKSPACE){
         current_string_builder.deleteCharAt(current_string_builder.length() - 1);
      } else if(latest_key.getKey() >= 'a' || latest_key.getKey() <= 'z' ||
         latest_key.getKey() >= 'A' || latest_key.getKey() <= 'Z' ||
         latest_key.getKey() >= '0' || latest_key.getKey() <= '9' ||
         latest_key.getKey() == '_' || latest_key.getKey() == '-'
      ){
         current_string_builder.append(latest_key.getKey());
      } else {
         return;
      }

      // Update string
      current_string = current_string_builder.toString();
   }


   private void finish_callback(){
      current_callback = null;
      current_string_builder = null;
   }


   /* ***** Processing Functions ***** */
   public void keyPressed(KeyEvent event) {
      int i = event.getKeyCode() % 512;

      keys_pressed[i] = true;
      latest_key = event;

      if(tracked_keyboard_keys[i] != null)
         tracked_keyboard_keys[i].pressed = true;

      if(current_callback != null)
         handle_callback();
   }


   public void keyReleased(KeyEvent event) {
      int i = event.getKeyCode() % 512;
      keys_pressed[i] = false;

      if(tracked_keyboard_keys[i] != null)
         tracked_keyboard_keys[i].pressed = false;
   }


   public void mousePressed(MouseEvent event) {
      int i = event.getButton() % 256;
      mouse_pressed[i] = true;

      if(tracker_mouse_keys[i] != null)
         tracker_mouse_keys[i].pressed = true;
      //System.out.println(event.getButton());
   }


   public void mouseReleased(MouseEvent event) {
      int i = event.getButton() % 256;
      mouse_pressed[event.getButton() % 256] = false;

      if(tracker_mouse_keys[i] != null)
         tracker_mouse_keys[i].pressed = false;
   }

   /* **** Key Sub Class **** */


   public static class Key {
      public boolean pressed;
   }
}

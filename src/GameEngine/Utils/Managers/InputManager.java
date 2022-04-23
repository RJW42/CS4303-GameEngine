package GameEngine.Utils.Managers;

import GameEngine.GameEngine;
import GameEngine.Triggers.InputComplete;
import GameEngine.Utils.FileUtils;
import processing.core.PConstants;
import processing.event.Event;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

public class InputManager {
   // Attributes
   public static final String[] REQUIRED_CONTROLS = new String[]{
      "UP", "DOWN", "LEFT", "RIGHT"
   };
   public static final int KEYS_SIZE = 512;
   public static final int MOUSE_SIZE = 128;
   private final GameEngine sys;

   public boolean[] keys_pressed;
   public boolean[] mouse_pressed;
   public KeyEvent latest_key;
   public MouseEvent latest_mouse;
   public Event latest_event;
   public String current_string;

   public Key mouse_click;

   private Key[] tracked_keyboard_keys;
   private Key[] tracker_mouse_keys;
   private HashMap<String, Key> key_mappings;

   private InputComplete current_callback;
   private StringBuilder current_string_builder;
   private int current_input_max_length;


   // Constructor
   public InputManager(GameEngine sys) {
      this.sys = sys;
      this.keys_pressed = new boolean[KEYS_SIZE];
      this.mouse_pressed = new boolean[MOUSE_SIZE];
      this.tracked_keyboard_keys = new Key[KEYS_SIZE];
      this.tracker_mouse_keys = new Key[MOUSE_SIZE];

      // Load in the controls
      if(!init(GameEngine.CONFIG_FOLDER + GameEngine.CONTROLS_FILE)){
         // Failed to read try reading the defaults controls
         System.out.println(" - Attempting to read the default controls file");
         if(!init(GameEngine.DEFAULT_CONFIG_FOLDER + GameEngine.DEFAULT_CONTROLS_FILE)){
            System.err.println(" - Failed to read in the default controls");
            System.exit(0);
         }

         // Save the defaults
         System.out.println(" - Read in the defaults saving");
         reset_to_defaults();
      }

      // Init attributes
      this.mouse_click = (tracker_mouse_keys[PConstants.LEFT] == null) ? (tracker_mouse_keys[PConstants.LEFT] = new Key()) : tracker_mouse_keys[PConstants.LEFT];
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
      // Upper case the name to prevent issues with that
      name = name.toUpperCase();

      // check if set
      if(key_mappings.containsKey(name)){
         return key_mappings.get(name);
      }

      // Not set err and close
      System.err.println("Could not find mapping for key: " + name);
      System.exit(0);
      return null;
   }


   public void setMapping(){
      // Todo: take name change button
      //       may want to implment this in future
   }

   public boolean getInput(InputComplete callback, int max_length){
      return getInput(callback, max_length, "");
   }

   public boolean getInput(InputComplete callback, int max_length, String starting_string){
      // Check if, can track input
      if(current_callback != null)
         return false; // Can's start tracking an input as already doing so

      // Start tracking input from keyboard
      current_input_max_length = max_length;
      current_callback = callback;
      current_string = starting_string;
      current_string_builder = new StringBuilder(starting_string);

      return true;
   }


   public void cancelInput(){
      finish_callback();
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
         if(current_string_builder.length() > 0)
            current_string_builder.deleteCharAt(current_string_builder.length() - 1);
         else
            return;
      } else if(
         (latest_key.getKey() >= 'a' && latest_key.getKey() <= 'z') ||
         (latest_key.getKey() >= 'A' && latest_key.getKey() <= 'Z') ||
         (latest_key.getKey() >= '0' && latest_key.getKey() <= '9') ||
         latest_key.getKey() == '_' || latest_key.getKey() == '-'
      ){
         if (current_string_builder.length() < current_input_max_length)
            // Check current string not to long
            current_string_builder.append(latest_key.getKey());
         else
            return;
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
      int i = event.getKeyCode() % KEYS_SIZE;
      // String s = getEventString(event);

      // Record this key press
      keys_pressed[i] = true;
      latest_key = event;
      latest_event = event;

      // Update the key object if one is set
      if(tracked_keyboard_keys[i] != null)
         tracked_keyboard_keys[i].pressed = true;

      // Handle any callbacks if needed
      if(current_callback != null)
         handle_callback();
   }


   public void keyReleased(KeyEvent event) {
      int i = event.getKeyCode() % KEYS_SIZE;

      // Unset this key
      keys_pressed[i] = false;

      // Unset the key object if one is set
      if(tracked_keyboard_keys[i] != null)
         tracked_keyboard_keys[i].pressed = false;
   }


   public void mousePressed(MouseEvent event) {
      int i = event.getButton() % MOUSE_SIZE;

      // Record this mouse press
      mouse_pressed[i] = true;
      latest_mouse = event;
      latest_event = event;

      // Update the key object if one is set
      if(tracker_mouse_keys[i] != null)
         tracker_mouse_keys[i].pressed = true;
   }


   public void mouseReleased(MouseEvent event) {
      int i = event.getButton() % MOUSE_SIZE;

      // Unset this mouse press
      mouse_pressed[i] = false;

      // Update the key object if one set
      if(tracker_mouse_keys[i] != null)
         tracker_mouse_keys[i].pressed = false;
   }

   /* **** Key Sub Class **** */

   /**
    * The key class represents a key on the keyboard. It contains a single
    * attribute pressed. This is true or false for if a key is pressed. This
    * class exists at it is essentially a pointer to a boolean preventing the
    * need for method calls.
    */
   public static class Key {
      public boolean pressed;
   }


   /* **** Control file parsing ***** */
   private static final String MOUSE_LEFT    = "MOUSE_LEFT";
   private static final String MOUSE_RIGHT   = "MOUSE_RIGHT";
   private static final String MOUSE_MIDDLE  = "MOUSE_MIDDLE";
   private static final String SPACE         = "SPACE";
   private static final String CONTROL       = "CONTROL";
   private static final String SHIFT         = "SHIFT";
   private static final String TAB           = "TAB";


   public void reset_to_defaults(){
      if(FileUtils.copyAndReplaceFile(
              GameEngine.DEFAULT_CONFIG_FOLDER + GameEngine.DEFAULT_CONTROLS_FILE,
              GameEngine.CONFIG_FOLDER + GameEngine.CONTROLS_FILE
      )) {
         return;
      }
      // Failed to reset to default
      System.err.println(" Failed to reset control file to default ");
   }


   public void save(){
      // Todo: implement this if it is wanted
      //       it should be called by set mappings
   }


   private boolean init(String file_loc){
      // Init mappings
      key_mappings = new HashMap<>();

      // Open the file
      System.out.println("Reading controls file");
      boolean parsed_controls = FileUtils.parseConfigFile(file_loc).map(
         pairs -> pairs
            .stream()
            .reduce(true, (val, pair) -> {
               // Parse each pair in the controls file
               if(!parse_line(pair.first, pair.second)){
                  return false;
               }
               return val;
            }, Boolean::logicalOr)
      ).orElse(false);

      // Check if the controls have been parsed
      if(!parsed_controls){
         return false;
      }

      // Check all desired controls have been parsed
      for(String control : REQUIRED_CONTROLS){
         if(!key_mappings.containsKey(control)) {
            System.err.println(" - Controls file missing mapping for: " + control);
            return false;
         }
      }
      return true;
   }


   public String getEventString(Event event){
      // Check if this is a keyboard or mouse event
      if (event instanceof KeyEvent){
         return get_key_event_string((KeyEvent) event);
      }else if(event instanceof MouseEvent){
         return get_mouse_event_string((MouseEvent) event);
      }
      System.err.println("Invalid event class found in get event string");
      System.exit(0);
      return null;
   }


   private boolean parse_line(String key, String value){
      // Parse the value
      return get_key_code(value).map(key_index -> {
         // Valid key index
         if(key_mappings.containsKey(key))
            return true;

         // Create key and continue
         Key key_ref = tracked_keyboard_keys[key_index] == null ? new Key() : tracked_keyboard_keys[key_index];
         key_ref.pressed = false;

         tracked_keyboard_keys[key_index] = key_ref;
         key_mappings.put(key, key_ref);

         return true;
      }).orElseGet(() -> get_mouse_code(value).map(mouse_index -> {
         // Valid key index
         if(key_mappings.containsKey(key))
            return true;

         // Create key and continue
         Key key_ref = new Key();
         key_ref.pressed = false;

         tracker_mouse_keys[mouse_index] = key_ref;
         key_mappings.put(key, key_ref);

         return true;
      }).orElse(false));
   }


   private String get_key_event_string(KeyEvent event){
      // Check what type of key has been pressed
      if(event.getKeyCode() >= 'A' && event.getKeyCode() <= 'Z'){
         return "" + ((char)event.getKeyCode()); // Alpha key
      }else if(event.getKeyCode() >= '0' && event.getKeyCode() <= '9'){
         return "" + ((char)event.getKeyCode()); // Numeric key
      }else if(event.getKey() == ' ') {
         return SPACE;
      }else if(event.getKeyCode() == PConstants.CONTROL){
         return CONTROL;
      }else if(event.getKeyCode() == PConstants.SHIFT){
         return SHIFT;
      }else if(event.getKey() == PConstants.TAB){
         return TAB;
      }else{
         // Unknown key use key code
         return "KEY_" + (event.getKeyCode() % KEYS_SIZE);
      }
   }


   private Optional<Integer> get_key_code(String key_string){
      if(key_string.startsWith("KEY_")){
         return get_generic_key_code(key_string.substring(4));
      }else if(key_string.equals(SPACE)){
         return Optional.of((int)' ');
      }else if(key_string.equals(CONTROL)){
         return Optional.of(PConstants.CONTROL);
      }else if(key_string.equals(SHIFT)){
         return Optional.of(PConstants.SHIFT);
      }else if(key_string.equals(TAB)){
         return Optional.of((int)PConstants.TAB);
      }else if(key_string.length() == 1){
         return get_alpha_numeric_key_code(key_string);
      }
      return Optional.empty();
   }


   private Optional<Integer> get_generic_key_code(String key_string){
      try{
         // Attempt to parse
         return Optional.of(Integer.parseInt(key_string));
      }catch (Exception e) {
         // Failed to parse
         System.err.println(" - Invalid key code must be an int");
         System.err.println(" - " + e.getMessage());
         return Optional.empty();
      }
   }


   private Optional<Integer> get_alpha_numeric_key_code(String key_string){
      // Get the character
      char c = key_string.charAt(0);
      // Check if alphabet character
      if(c >= 'A' && c <= 'Z'){
         return Optional.of((int)c);
      }else if(c >= '0' && c <= '9'){
         return Optional.of((int)c);
      }
      return Optional.empty();
   }


   private String get_mouse_event_string(MouseEvent event){
      // Simple method, Processing only supports three mouse events
      switch (event.getButton()){
         case PConstants.LEFT:
            return MOUSE_LEFT;
         case PConstants.RIGHT:
            return MOUSE_RIGHT;
         default:
            return MOUSE_MIDDLE;
      }
   }


   private Optional<Integer> get_mouse_code(String mouse_string){
      // Simple method, Processing only supports three mouse events
      if(mouse_string.equals(MOUSE_LEFT)){
         return Optional.of(PConstants.LEFT);
      }else if(mouse_string.equals(MOUSE_RIGHT)){
         return Optional.of(PConstants.RIGHT);
      }else if(mouse_string.equals(MOUSE_MIDDLE)){
         return Optional.of(PConstants.CENTER);
      }else{
         return Optional.empty();
      }
   }
}

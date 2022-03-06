package GameEngine;

import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.CollisionHandlers.CollisionHandler;
import GameEngine.GameObjects.*;
import GameEngine.Levels.LevelManager;
import GameEngine.Levels.TestLevel;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GameEngine extends PApplet {
   public static final int Z_LAYERS       = 4;
   public static final int SCREEN_WIDTH   = 896;
   public static final int SCREEN_HEIGHT  = 896;
   public static final int GRID_SIZE      = 32;
   public static final int GRID_X_SIZE    = SCREEN_WIDTH / GRID_SIZE;
   public static final int GRID_Y_SIZE    = SCREEN_HEIGHT / GRID_SIZE;
   public static int PIXEL_TO_METER       = 32;
   public static int WORLD_WIDTH          = SCREEN_WIDTH / PIXEL_TO_METER;
   public static int WORLD_HEIGHT         = SCREEN_HEIGHT / PIXEL_TO_METER;

   public ScoreManager score_manager = null;
   public boolean DISPLAY_FPS    = true;
   public boolean DISPLAY_BOUNDS = true;
   public boolean DISPLAY_COLS   = true;
   public boolean ENABLE_PAUSE   = true;
   public int TARGET_FPS         = 60;
   public float GRAVITY          = 9.8f;
   public float DELTA_TIME       = 1f / TARGET_FPS; // 1 / DELTA_TIME == current FPS
   public float TOTAL_TIME       = 0f;
   public float TIME_FACTOR      = 1f;

   /* Game Engine Variables */
   public Minim minim;
   public InputManager input_manager;

   private boolean pause;
   private boolean frame_skip;
   private long pause_time;
   private long prev;

   private ArrayList<GameObject>[] objects;
   private ArrayList<GameObject>[] objects_to_add;
   private ArrayList<BaseCollisionComponent>[] collision_grid;
   private HashMap<BaseCollisionComponent, HashSet<BaseCollisionComponent>> collisions;
   private LevelManager level_manager;

   // https://www.redblobgames.com/articles/visibility/

   public static void main(String[] args) {
      PApplet.main("GameEngine.GameEngine");
   }

   public void settings(){
      size(SCREEN_WIDTH, SCREEN_HEIGHT);
   }

   public void setup() {
      // System Setup
      frameRate(TARGET_FPS);
      pause = false;
      frame_skip = false;

      // Init Game Engine Variables
      prev = System.nanoTime() - (long)(DELTA_TIME * 1e9);
      minim = new Minim(this);

      // Init level manager
      level_manager = new LevelManager(this, new TestLevel(this));

      // Init input manager
      input_manager = new InputManager(this);
   }

   public void initWorld(){
      initWorld(WORLD_WIDTH, WORLD_HEIGHT);
   }

   public void initWorld(int world_width, int world_height){
      // Init World Sizes
      WORLD_HEIGHT = world_height;
      WORLD_WIDTH = world_width;

      // reset grid sizes
      clearGameObjects();
      clearCollsionObjects();
   }

   public void draw() {
      // Handle any pause mechanics
      handlePause();

      if(pause)
         return;

      // Calculate Delta Time
      calculateDeltaTime();

      // Draw backgorund
      level_manager.drawBackground();

      // Draw any debug graphics
      debug();

      // Update Game Objects
      updateGameObjects();

      // Handle any collisions
      resolveGameObjectCollisions();

      // Draw game objects
      drawGameObjects();

      // Add any new game objects to the game world
      spawnGameObjects();

      // Check if the level should advance
      level_manager.update();
   }

   public void stop(){
      // Close Audio
      minim.stop();

      // Ensure normal closer occurs
      super.stop();
   }

   /**
    * Adds a new game object to the objects list.
    *
    * Note: Objects will be added at the end of the current frame
    * @param object The new object to add
    * @param z_level The draw level for this object
    */
   public void spawn(GameObject object, int z_level){
      this.objects_to_add[z_level].add(object);
   }

   public void clearGameObjects(){
      objects = new ArrayList[Z_LAYERS];
      objects_to_add = new ArrayList[Z_LAYERS];
      for(int i = 0; i < Z_LAYERS; i++) {
         objects[i] = new ArrayList<>();
         objects_to_add[i] = new ArrayList<>();
      }
   }

   public void clearCollsionObjects(){
      this.collision_grid = new ArrayList[GRID_X_SIZE * GRID_Y_SIZE];
      this.collisions = new HashMap<>();
      for(int i = 0; i < GRID_X_SIZE * GRID_Y_SIZE; i++)
         this.collision_grid[i] = new ArrayList<>(2);
   }

   /* ******** Private Update Methods ******** */
   private void calculateDeltaTime(){
      long curr = System.nanoTime();
      DELTA_TIME = ((float) ((curr - prev) / 1.0e9)) * TIME_FACTOR;
      TOTAL_TIME += DELTA_TIME;
      prev = curr;
   }

   private void drawGameObjects(){
      for(int i = 0; i < Z_LAYERS; i++)
         for(int j = objects[i].size() - 1; j >=0; j--)
            objects[i].get(j).draw();
   }

   private void spawnGameObjects(){
      for(int i = 0; i < Z_LAYERS; i++)
         for(int j = objects_to_add[i].size() - 1; j >= 0; j--)
            objects[i].add(objects_to_add[i].remove(j));
   }

   private void updateGameObjects(){
      for(int i = 0; i < Z_LAYERS; i++)
         for(int j = objects[i].size() - 1; j >=0; j--) {
            GameObject obj = objects[i].get(j);
            obj.update();

            // If object is dead remove from both object list and collision grid
            if(obj.isDestroyed()) {
               objects[i].remove(j);

               if(obj instanceof Collideable){
                  ((Collideable) obj).getCollisionComponents().forEach(BaseCollisionComponent::clearCollsionGrids);
               }
            } // Object Not Dead Update Grid Position
            else if(obj instanceof Collideable)
               ((Collideable) obj).getCollisionComponents().forEach(BaseCollisionComponent::updateCollisionGrids);
         }
   }

   private void resolveGameObjectCollisions(){
      this.collisions.clear();
      for(int i = 0; i < GRID_X_SIZE * GRID_Y_SIZE; i++) {
         ArrayList<BaseCollisionComponent> collision_square = collision_grid[i];

         if(collision_square.size() > 0 && DISPLAY_BOUNDS)
            displayGridCell(i % GRID_X_SIZE, i / GRID_Y_SIZE, 0, 255, 0);

         if (collision_square.size() > 1) {
            handleCollision(collision_square);

            if(DISPLAY_BOUNDS)
               displayGridCell(i % GRID_X_SIZE, i / GRID_Y_SIZE, 255, 0, 0);
         }
      }
   }

   /* ***** Collision Handling Functions ***** */
   private void handleCollision(ArrayList<BaseCollisionComponent> collision_square){
      // For Each element in this square check if they are colliding
      for(int i = 0; i < collision_square.size(); i++){
         for(int j = i; j < collision_square.size(); j++){
            // Todo: need to add the can collide thing. Or could maybe do that in the collides with method

            // Get two objects that could be colliding
            BaseCollisionComponent obj1 = collision_square.get(i);
            BaseCollisionComponent obj2 = collision_square.get(j);

            // Check if the collision has not already occurred
            if(collision_occurred(obj1, obj2))
               continue;

            // Check and handle collision
            if(obj1.collidesWith(obj2)){
               // Store this collision to prevent it occurring again
               collisions.putIfAbsent(obj1, new HashSet<>());
               collisions.get(obj1).add(obj2);

               // Handle Triggers
               if(obj1.isTrigger()){
                  obj1.trigger(obj2);
               }

               if(obj2.isTrigger()){
                  obj2.trigger(obj1);
               }

               // Handle Collision
               CollisionHandler.handle_collision(obj1, obj2);
            }
         }
      }
   }

   private boolean collision_occurred(BaseCollisionComponent obj1, BaseCollisionComponent obj2){
      if(collisions.containsKey(obj1)){
         return  collisions.get(obj1).contains(obj2);
      }
      if(collisions.containsKey(obj2)){
         return  collisions.get(obj2).contains(obj1);
      }
      return false;
   }

   public int getGridIndex(int grid_x, int grid_y){
      return (GRID_Y_SIZE * grid_y) + grid_x;
   }

   public void setGridObject(int index, BaseCollisionComponent obj){
      this.collision_grid[index].add(obj);
   }

   public void removeGridObject(int index, BaseCollisionComponent obj){
      this.collision_grid[index].remove(obj);
   }

   /* ***** Debug ***** */
   public void debug(){
      if(DISPLAY_FPS){
         fill(255);
         textSize(16);
         text("FPS: " + (int)(1f / DELTA_TIME), 800, 32);
      }

      if(DISPLAY_COLS){
         drawGridLines();
      }
   }

   public void displayGridCell(int grid_x, int grid_y, int r, int g, int b){
      noFill();
      stroke(r, g, b);
      square(grid_x * GameEngine.GRID_SIZE, GameEngine.SCREEN_HEIGHT - ((grid_y + 1) * GameEngine.GRID_SIZE), GameEngine.GRID_SIZE);
   }

   private void drawGridLines(){
      stroke(127);
      strokeWeight(1);
      for(int i = GRID_SIZE; i < SCREEN_WIDTH; i += GRID_SIZE){
         line(i, 0, i, SCREEN_WIDTH);
         line(0, i, SCREEN_HEIGHT, i);
      }
   }

   private void handlePause(){
      // Check if should pause
      if((!pause && input_manager.keys_pressed[192]) || frame_skip){
         // Pause game
         pause_time = System.nanoTime();
         pause = true;
         input_manager.keys_pressed[192] = false;
         frame_skip = false;
         return;
      }

      // Check if should unpause
      if(pause && input_manager.keys_pressed[192]){
         // Un pause game
         pause = false;
         prev = System.nanoTime() - (pause_time - prev);
         input_manager.keys_pressed[192] = false;
         return;
      }

      // Check if should advance by one frame only
      if(pause && input_manager.keys_pressed[9]){
         // Un pause for one frame
         pause = false;
         prev = System.nanoTime() - (pause_time - prev);
         input_manager.keys_pressed[9] = false;
         frame_skip = true;
         return;
      }
   }

   /* ***** KEY PRESSES ***** */
   @Override
   public void keyPressed(KeyEvent event) {
      input_manager.keyPressed(event);
   }

   @Override
   public void keyReleased(KeyEvent event) {
      input_manager.keyReleased(event);
   }

   @Override
   public void mousePressed(MouseEvent event) {
      input_manager.mousePressed(event);
   }

   @Override
   public void mouseReleased(MouseEvent event) {
      input_manager.mouseReleased(event);
   }

   /* ***** Math Functions ***** */
   // Hyperbolics Taken From https://introcs.cs.princeton.edu/java/22library/Hyperbolic.java.html
   public static double cosh(double x) {
      return (Math.exp(x) + Math.exp(-x)) / 2.0;
   }

   public static double sinh(double x) {
      return (Math.exp(x) - Math.exp(-x)) / 2.0;
   }

   public static double tanh(double x) {
      return sinh(x) / cosh(x);
   }

   public static double atanh(double a) {
      //www.java2s.com/example/java-utility-method/atanh/atanh-double-a-fb896.html
      final double mult;
      // check the sign bit of the raw representation to handle -0
      if (Double.doubleToRawLongBits(a) < 0) {
         a = Math.abs(a);
         mult = -0.5d;
      } else {
         mult = 0.5d;
      }
      return mult * Math.log((1.0d + a) / (1.0d - a));
   }

   public static double acosh(double x) {
      // http://www.java2s.com/example/java-utility-method/acosh/acosh-double-x-02b94.html
      double ans;

      if (Double.isNaN(x) || (x < 1)) {
         ans = Double.NaN;
      }
      // 94906265.62 = 1.0/Math.sqrt(EPSILON_SMALL)

      else if (x < 94906265.62) {
         ans = safeLog(x + Math.sqrt(x * x - 1.0D));
      } else {
         ans = 0.69314718055994530941723212145818D + safeLog(x);
      }

      return ans;
   }

   public static double safeLog(double x) {
      if (x == 0.0D) {
         return 0.0D;
      } else {
         return Math.log(x);
      }
   }

   /* ***** Processing Overrides ***** */
   
}

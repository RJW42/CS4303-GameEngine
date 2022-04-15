package GameEngine;

import GameEngine.Components.CollisionComponents.BaseCollisionComponent;
import GameEngine.Components.CollisionComponents.Collideable;
import GameEngine.Components.CollisionComponents.CollisionHandlers.CollisionHandler;
import GameEngine.Components.CollisionComponents.RectCollisionComponent;
import GameEngine.GameObjects.*;
import GameEngine.Levels.*;
import GameEngine.Utils.Config;
import GameEngine.Utils.Managers.AudioManager;
import GameEngine.Utils.Managers.InputManager;
import GameEngine.Utils.Managers.ImageManager;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.*;
import java.util.stream.Collectors;

public class GameEngine extends PApplet {
   public static final String DEFAULT_CONFIG_FOLDER   = "GameEngine/Resources/Defaults/";
   public static final String DEFAULT_CONTROLS_FILE   = "default_controls.txt";
   public static final String DEFAULT_CONFIG_FILE     = "default_config.txt";
   public static final String SAVES_LOCATION = "GameEngine/Resources/Saves/scoreboard.csv";
   public static final String SPRITE_FOLDER  = "GameEngine/Resources/Sprites";
   public static final String GIFS_FOLDER    = "GameEngine/Resources/Gifs";
   public static final String CONFIG_FOLDER  = "GameEngine/Resources/";
   public static final String CONFIG_FILE    = "config.txt";
   public static final String CONTROLS_FILE   = "controls.txt";

   public static final int Z_LAYERS          = 4;
   public static final float GRID_SIZE       = 1;
   public static float PIXEL_TO_METER_X      = -1;
   public static float PIXEL_TO_METER_Y      = -1;
   private static int SCREEN_HEIGHT          = 960;
   private static int SCREEN_WIDTH           = 960;
   public static int WORLD_HEIGHT            = 18;
   public static int WORLD_WIDTH             = 32;
   public static int GRID_X_SIZE             = WORLD_WIDTH / (int)GRID_SIZE;
   public static int GRID_Y_SIZE             = WORLD_HEIGHT / (int)GRID_SIZE;
   public static int TARGET_FPS              = 60;

   public boolean DISPLAY_BOUNDS          = false;
   public boolean DISPLAY_COLS            = false;
   public boolean ENABLE_PAUSE            = false;
   public boolean DISPLAY_FPS             = false;
   public float TIME_FACTOR               = 1f;
   public float DELTA_TIME                = 1f / TARGET_FPS; // 1 / DELTA_TIME == current FPS
   public float TOTAL_TIME                = 0f;
   public float GRAVITY                   = 16f;

   /* Game Engine Variables */
   private Minim minim;
   public InputManager input_manager;
   public ImageManager sprite_manager;
   public ScoreManager score_manager;
   public AudioManager audio_manager;
   public GameObject chase_object;
   public float chase_zoom;
   public Config config;
   public float mouse_x;
   public float mouse_y;

   private boolean pause;
   private boolean frame_skip;
   private long pause_time;
   private long prev;

   private ArrayList<GameObject>[] objects;
   private ArrayList<GameObject>[] objects_to_add;
   private ArrayList<BaseCollisionComponent>[] collision_grid;
   private HashMap<BaseCollisionComponent, HashSet<BaseCollisionComponent>> collisions;
   private LevelManager level_manager;

   /* Game Specific Variables */
   public Terrain terrain;

   // https://www.redblobgames.com/articles/visibility/
   public static void main(String[] args) {
      PApplet.main("GameEngine.GameEngine");
   }


   public void settings(){
      // Load config
      config = new Config();

      // Init screen size
      if(config.full_screen){
         fullScreen(config.display);
         SCREEN_WIDTH = displayWidth;
         SCREEN_HEIGHT = displayHeight;
      }else{
         size(config.screen_width, config.screen_height);
         SCREEN_WIDTH = config.screen_width;
         SCREEN_HEIGHT = config.screen_height;
         WORLD_HEIGHT = config.world_height;
         WORLD_WIDTH = config.world_width;
      }

      // Update collision grid size
      GRID_X_SIZE = WORLD_WIDTH / (int)GRID_SIZE;
      GRID_Y_SIZE = WORLD_HEIGHT / (int)GRID_SIZE;

      // Use screen size to init constants
      PIXEL_TO_METER_X = (float)SCREEN_WIDTH / WORLD_WIDTH;
      PIXEL_TO_METER_Y = (float)SCREEN_HEIGHT / WORLD_HEIGHT;

      // Set default zoom
      chase_zoom = 3f;
   }


   public void setup() {
      // System Setup
      frameRate(TARGET_FPS);

      // Init Game Engine Variables
      pause = false;
      frame_skip = false;
      prev = System.nanoTime() - (long)(DELTA_TIME * 1e9);

      // Init manager objects
      minim = new Minim(this);
      sprite_manager = new ImageManager(this);
      input_manager = new InputManager(this);
      audio_manager = new AudioManager(this, minim);

      // Init level manager
      level_manager = new LevelManager(this, new MapBuilder(this, 60, 60));
      //level_manager = new LevelManager(this, new TestLevel(this));
   }


   public void initWorld(){
      initWorld(WORLD_WIDTH, WORLD_HEIGHT);
   }


   public void initWorld(int world_width, int world_height){
      // Init World Sizes
      WORLD_HEIGHT = world_height;
      WORLD_WIDTH = world_width;
      GRID_X_SIZE = world_width;
      GRID_Y_SIZE = world_height;

      // Use screen size to init constants
      PIXEL_TO_METER_X = (float)SCREEN_WIDTH / WORLD_WIDTH;
      PIXEL_TO_METER_Y = (float)SCREEN_HEIGHT / WORLD_HEIGHT;

      // reset grid sizes
      clearGameObjects();
      clearCollsionObjects();
   }


   public void draw() {
      // Scale screen from metric to pixel
      scale_screen();

      // Handle any pause mechanics
      if(ENABLE_PAUSE)
         handlePause();

      if(pause)
         return;

      // Calculate Delta Time
      calculateDeltaTime();

      // Draw backgorund
      level_manager.drawBackground();

      // Update Game Objects
      updateGameObjects();

      // Handle any collisions
      resolveGameObjectCollisions();

      // Draw game objects
      drawGameObjects();

      // Draw any debug graphics
      debug();

      // Add any new game objects to the game world
      spawnGameObjects();

      // Check if the level should advance
      level_manager.update();
   }
   
   
   private void scale_screen(){
      // Apply default scaling
      strokeWeight(1 / PIXEL_TO_METER_X);
      scale(PIXEL_TO_METER_X, PIXEL_TO_METER_Y); // Set to metric

      scale(1f, -1f); // Set 0,0 to bottom left
      translate(0, -WORLD_HEIGHT);

      // Get Mouse Posistion
      mouse_x = mouseX / PIXEL_TO_METER_X;
      mouse_y = WORLD_HEIGHT - (mouseY / PIXEL_TO_METER_Y);

      // Chase object if set
      if(chase_object == null)
         return;

      PVector chase_pos = chase_object.pos;
      float translate_x = (WORLD_WIDTH  / 2f - chase_pos.x * chase_zoom);
      float translate_y = (WORLD_HEIGHT / 2f - chase_pos.y * chase_zoom);
      translate(translate_x, translate_y);

      mouse_x = chase_pos.x - WORLD_WIDTH / (chase_zoom * 2) + ((float) mouseX / SCREEN_WIDTH) * (WORLD_WIDTH / chase_zoom);
      mouse_y = chase_pos.y - WORLD_WIDTH / (chase_zoom * 2) + (1 - (float) mouseY / SCREEN_WIDTH) * (WORLD_WIDTH / chase_zoom);

      scale(chase_zoom);
   }


   @Override
   public void text(String str, float x, float y) {
      pushMatrix();
      translate(0, WORLD_HEIGHT);
      scale(1f, -1f);
      scale(1f / PIXEL_TO_METER_X, 1f / PIXEL_TO_METER_Y);
      super.text(str, x * PIXEL_TO_METER_X, GameEngine.SCREEN_HEIGHT - (y * PIXEL_TO_METER_Y));
      popMatrix();
   }


   @Override
   public void image(PImage img, float x, float y){
      pushMatrix();
      translate(0, WORLD_HEIGHT);
      scale(1f, -1f);
      scale(1f / PIXEL_TO_METER_X, 1f / PIXEL_TO_METER_Y);
      translate(x * PIXEL_TO_METER_X, GameEngine.SCREEN_HEIGHT - (y * PIXEL_TO_METER_Y));
      super.image(img, -img.width/2, -img.height/2);
      popMatrix();
   }


   public void image(PImage img, float x, float y, float angle){
      pushMatrix();
      translate(0, WORLD_HEIGHT);
      scale(1f, -1f);
      scale(1f / PIXEL_TO_METER_X, 1f / PIXEL_TO_METER_Y);
      translate(x * PIXEL_TO_METER_X, GameEngine.SCREEN_HEIGHT - (y * PIXEL_TO_METER_Y));
      rotate(angle);
      super.image(img, -img.width/2, -img.height/2);
      popMatrix();
   }


   public void stop(){
      // Close Audio
      audio_manager.stop();
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
      // Spawn all objects
      ArrayList<GameObject> objects_to_start = new ArrayList<>();

      for(int i = 0; i < Z_LAYERS; i++) {
         for (int j = objects_to_add[i].size() - 1; j >= 0; j--) {
            GameObject obj = objects_to_add[i].get(j);
            objects[i].add(obj);
            objects_to_start.add(obj);
         }
         objects_to_add[i].clear();
      }

      // Start all game objects
      objects_to_start.forEach(GameObject::start);
   }


   private void updateGameObjects(){
      for(int i = 0; i < Z_LAYERS; i++)
         for(int j = objects[i].size() - 1; j >=0; j--) {
            GameObject obj = objects[i].get(j);
            obj.update();

            // If object is dead remove from both object list and collision grid
            if(obj.isDestroyed()) {
               obj.onDeath();
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

      CollisionHandler.start_collisions();

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

      CollisionHandler.end_collisions();
   }


   /* ***** Object Managing Functions ***** */
   @SuppressWarnings("unchecked")
   public <T extends GameObject> T getGameObject(Class<T> type){
      return (T) Arrays
         .stream(objects)
         .flatMap(List::stream)
         .filter(type::isInstance)
         .findAny()
         .orElse(null);
   }


   @SuppressWarnings("unchecked")
   public <T extends GameObject> List<T> getGameObjects(Class<T> type){
      return (List<T>) Arrays
         .stream(objects)
         .flatMap(List::stream)
         .filter(type::isInstance)
         .collect(Collectors.toList());
   }


   /* ***** Collision Handling Functions ***** */
   private void handleCollision(ArrayList<BaseCollisionComponent> collision_square){
      // For Each element in this square check if they are colliding
      for(int i = 0; i < collision_square.size(); i++){
         for(int j = i + 1; j < collision_square.size(); j++){
            // Get two objects that could be colliding
            BaseCollisionComponent obj1 = collision_square.get(i);
            BaseCollisionComponent obj2 = collision_square.get(j);

            // Objects can't collide with themselves
            if(obj1.parent == obj2.parent)
               continue;

            // Check if the collision has not already occurred
            if(collision_occurred(obj1, obj2))
               continue;

            // Check and handle collision
            if(obj1.collidesWith(obj2)){
               // Store this collision to prevent it occurring again
               record_collision(obj1, obj2);

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


   private void record_collision(BaseCollisionComponent obj1, BaseCollisionComponent obj2){
      collisions.putIfAbsent(obj1, new HashSet<>());
      collisions.get(obj1).add(obj2);
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
         fill(255, 0, 0);
         textSize(20);
         text("FPS: " + (int)(1f / DELTA_TIME), WORLD_WIDTH - 2, WORLD_HEIGHT - 1);
      }

      if(DISPLAY_COLS){
         drawGridLines();
      }

      for(int i = 0; i < GRID_X_SIZE * GRID_Y_SIZE; i++) {
         ArrayList<BaseCollisionComponent> collision_square = collision_grid[i];

         if(collision_square.size() > 0 && DISPLAY_BOUNDS) {
            displayGridCell(i % GRID_X_SIZE, i / GRID_Y_SIZE, 0, 255, 0);

            if (DISPLAY_BOUNDS)
               displayGridCell(i % GRID_X_SIZE, i / GRID_Y_SIZE, 255, 0, 0);
         }
      }
   }


   public void displayGridCell(float grid_x, float grid_y, int r, int g, int b){
      noFill();
      stroke(r, g, b);
      shapeMode(PApplet.CORNER);
      square(grid_x, grid_y, GameEngine.GRID_SIZE);
   }


   private void drawGridLines(){
      stroke(127);
//      for(int i = GRID_SIZE; i < WORLD_HEIGHT; i += GRID_SIZE){
//         line(i, 0, i, SCREEN_WIDTH);
//         line(0, i, SCREEN_HEIGHT, i);
//      }
      for(float i = GRID_SIZE; i < WORLD_HEIGHT; i += GRID_SIZE){
         strokeWeight(1f / PIXEL_TO_METER_Y);
         line(i, 0, i, WORLD_WIDTH);
         strokeWeight(1f / PIXEL_TO_METER_X);
         line(0, i, WORLD_HEIGHT, i);
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
}

package GameEngine.Components.AIComponents;


import GameEngine.Components.Component;
import GameEngine.Components.ForceManager;
import GameEngine.Components.PlayerComponents.GunController;
import GameEngine.GameEngine;
import GameEngine.GameObjects.Core.Monster;
import GameEngine.GameObjects.Core.Player;
import GameEngine.GameObjects.Core.Terrain;
import GameEngine.GameObjects.GameObject;
import processing.core.PConstants;
import processing.core.PVector;

import static GameEngine.Levels.PlayLevel.DESIRED_WALLS;


public class Timer extends Component {
   // Attributes
   public static final int FULL_SCREEN_TIME = 1;
   public static final int ZOOM_TIME = 3;
   public static boolean ACTIVE = true;

   private final float final_zoom;
   private final float total_distance;

   private final PVector direction;
   private final PVector start_loc;
   private final Player player;

   private boolean game_started;
   private float time_remaining;

   public long game_start_time;
   public float game_time;


   // Constructor
   public Timer(GameObject parent, Player player) {
      super(parent);

      // Init attributes
      float desired_x_zoom = GameEngine.SCREEN_WIDTH / (DESIRED_WALLS * GameEngine.PIXEL_TO_METER);
      float desired_y_zoom = GameEngine.SCREEN_HEIGHT / (DESIRED_WALLS * GameEngine.PIXEL_TO_METER);

      this.final_zoom = Math.min(desired_x_zoom, desired_y_zoom);
      this.time_remaining = ZOOM_TIME + FULL_SCREEN_TIME;

      this.start_loc = new PVector(Terrain.WIDTH / 2f, Terrain.HEIGHT / 2f);
      this.total_distance = PVector.dist(start_loc, player.pos);
      this.direction = PVector.sub(player.pos, start_loc).normalize();
      this.player = player;
      this.game_started = false;

      sys.chase_position = start_loc;
   }


   // Methods 
   public void start() {
      Player.ACTIVE = false;
      Monster.ACTIVE = false;
      ForceManager.ACTIVE = false;
      sys.chase_zoom = 1f;
   }


   public void update() {
      // Check what state in
      if(!game_started) {
         handle_game_start();
         return;
      }

      if(ACTIVE) game_time = Math.round((System.nanoTime() - game_start_time) / 100000000f) / 10f;
   }


   public void draw() {
      if(time_remaining >= 0) {
         // Draw time to start
         sys.pushUI();
         sys.fill(255, 0, 0);
         sys.textSize(40);
         sys.uiText("T- " + Math.round(time_remaining * 10f) / 10f, GameEngine.SCREEN_WIDTH / 2f, GameEngine.SCREEN_HEIGHT / 2f);
         sys.popUI();
         return;
      }

      // Draw time in game
      sys.pushUI();
      sys.fill(255, 0, 0);
      sys.textSize(40);
      sys.textAlign(PConstants.LEFT);
      sys.uiText("T+ " + game_time, 50f, GameEngine.SCREEN_HEIGHT - sys.textAscent() - 50f);
      sys.popUI();
   }


   public void start_game(){
      sys.chase_zoom = final_zoom;
      sys.chase_position = player.pos;
      Player.ACTIVE = true;
      Monster.ACTIVE = true;
      ForceManager.ACTIVE = true;
      game_started = true;
      game_start_time = System.nanoTime();
   }


   private void handle_game_start(){
      // Update zoom amount and chase position
      if(time_remaining < 0)
         return;

      time_remaining -= sys.DELTA_TIME;

      if(time_remaining > ZOOM_TIME)
         return;

      // Check for end reached
      if(time_remaining < 0){
         // Game started
         start_game();
         return;
      }

      // Increase zoom and position
      float percentage = ((ZOOM_TIME - time_remaining) / ZOOM_TIME);
      sys.chase_zoom = 1f + (final_zoom - 1f) * percentage;
      sys.chase_position = PVector.add(start_loc, PVector.mult(direction, total_distance * percentage));
   }
}

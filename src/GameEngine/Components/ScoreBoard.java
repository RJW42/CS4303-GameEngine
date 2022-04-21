package GameEngine.Components;


import GameEngine.Components.Component;
import GameEngine.Components.Renderers.TextRenderer;
import GameEngine.GameEngine;
import GameEngine.GameObjects.GameObject;
import GameEngine.GameObjects.Terrain;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class ScoreBoard extends Component {
   // Attributes
   public int score;
   public String username = "";
   public boolean saved_score = false;

   private ArrayList<TextRenderer> text_lines;
   private LinkedHashMap<String, Integer> scores;
   private TextRenderer first_line;
   private KeyEvent event;
   private boolean sent_callback = false;


   // Constructor
   public ScoreBoard(GameObject parent, int score) {
      super(parent);

      // Init text renderer
      this.text_lines = new ArrayList<>(Terrain.HEIGHT - 4);
      this.score = score;

      // Init all text lines
      for(int i = 0; i < Terrain.HEIGHT - 4; i++){
         TextRenderer renderer   = new TextRenderer(parent, new PVector(4, Terrain.HEIGHT - 2 - i));
         renderer.text_align     = PApplet.CENTER;
         renderer.text           = "LINE: " + i;
         renderer.size           = 30;
         renderer.color          = new PVector(0, 0, 0);
         text_lines.add(i, renderer);
      }

      // Add Game Over Text
      first_line = text_lines.get(0);
      first_line.text = "Enter username: ";
      first_line.text_align = PApplet.CORNER;
      parent.addComponent(first_line);
   }


   // Methods 
   public void start() {
      // Load previous scores
      first_line.start();
      loadScores();
   }

   public void update() {
      if(saved_score)
         return;

      // Get username
      if(!sent_callback){
         sys.input_manager.getInput(this::username_callback, 10);
         sent_callback = true;
      }

      // Update the top text with the username
      first_line.text = "Enter Username: " + sys.input_manager.current_string;
   }

   public void draw() {
   }


   private void username_callback(String username, boolean escaped){
      // Todo: check if the username is valid less than 10 chars ect..
      this.username = username;
      saveScore();
      display_scores();
   }


   private void get_username(){
      // Check for a new keypress
      if(sys.input_manager.latest_key != null && !sys.input_manager.latest_key.equals(event)){
         event = sys.input_manager.latest_key;

         // Check is a valid key
         if(username.length() <= 10 &&
                 ((event.getKeyCode() >= 'A' && event.getKeyCode() <= 'Z') ||
                         (event.getKeyCode() >= '0' && event.getKeyCode() <= '9'))) {
            username += (char)event.getKeyCode();
         } else if(event.getKeyCode() == 8 && username.length() > 0){
            // Backspace pressed delete char
            username = username.substring(0, username.length() - 1);
         }else if(event.getKeyCode() == 10 && username.length() > 1) {
            // Enter Pressed Save Score
            saveScore();
         }
      }
   }


   private void display_scores(){
      // Add components to parent
      if(scores == null)
         return;

      // Get scores entry set
      var entries = new ArrayList<>(scores.entrySet());
      for(int i = 0; i < entries.size() && i < text_lines.size() - 1; i++){
         // Add scores
         var entry = entries.get(i);
         TextRenderer renderer = text_lines.get(i + 1);
         renderer.text        = entry.getKey() + " : " + entry.getValue();
         parent.addComponent(renderer);
         renderer.start();
      }

      // Update first line
      first_line.text = "Score Board";
      first_line.text_align = PApplet.CENTER;
   }


   private void saveScore(){
      // Load scores if needed
      if(scores == null)
         loadScores();

      // Check if username already in scoreboard
      if(!scores.containsKey(username) || scores.get(username) < score){
         scores.put(username, score);
      }

      // Update ordering
      sort();

      // Save to file
      try(PrintWriter pw = new PrintWriter(GameEngine.SAVES_LOCATION)){
         for(var entry : scores.entrySet()){
            pw.println(entry.getKey() + "," + entry.getValue());
         }
      } catch (IOException e){
         System.out.println("Failed to save score to savefile");
      }

      // Finished
      saved_score = true;
   }


   private void loadScores(){
      // Init Scores store
      scores = new LinkedHashMap<>();

      // Open File
      try(BufferedReader br = new BufferedReader(new FileReader(GameEngine.SAVES_LOCATION))){
         // Read each score
         String line;
         while((line = br.readLine()) != null){
            String[] pair = line.split(",");
            if(pair.length != 2)
               continue;
            String username = pair[0].trim();
            int score = Integer.parseInt(pair[1].trim());
            scores.put(username, score);
         }
      }catch (IOException e){
         // Failed to read scores
         System.out.println("Failed To Read Saves file");
      }catch (NumberFormatException e){
         // Saves file is invalid
         System.out.println("Invalid saves file");
      }

      // Sort Map
      sort();
   }

   private void sort(){
      // Sort the scores by score
      LinkedHashMap<String, Integer> sorted_map = new LinkedHashMap<>();

      scores.entrySet()
              .stream()
              .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
              .forEachOrdered(x -> sorted_map.put(x.getKey(), x.getValue()));

      scores = sorted_map;
   }
}

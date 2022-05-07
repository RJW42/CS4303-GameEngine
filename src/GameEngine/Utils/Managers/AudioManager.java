package GameEngine.Utils.Managers;

import GameEngine.GameEngine;
import ddf.minim.AudioPlayer;
import ddf.minim.AudioSample;
import ddf.minim.Minim;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class AudioManager {
   // Attributes
   public final GameEngine sys;
   public final Minim minim;

   private HashMap<String, String> sound_files;
   private HashMap<String, AudioSample> sound_samples;
   private HashMap<String, AudioPlayer> sound_players;

   private ArrayList<AudioPlayer> background_musics;
   private int current_background_index;


   // Constructor
   public AudioManager(GameEngine sys, Minim minim) {
      this.sys = sys;
      this.minim = minim;
      this.sound_files = new HashMap<>();
      this.sound_samples = new HashMap<>();
      this.sound_players = new HashMap<>();
      this.background_musics = new ArrayList<>();

      load_sounds();
   }


   // Methods
   public void stop(){
      // Close all audio files
      for(var entry : sound_samples.entrySet())
         entry.getValue().close();

      // Close minim
      minim.stop();
   }


   public AudioSample get_sample(String name){
      if(!sound_files.containsKey(name)) {
         System.err.println("Unable to find sound: " + name);
         System.exit(0);
         return null;
      }

      if(sound_samples.containsKey(name))
         return sound_samples.get(name);

      AudioSample sample = minim.loadSample(sound_files.get(name));
      sound_samples.put(name, sample);

      return sample;
   }


   public AudioPlayer get_player(String name){
      if(!sound_files.containsKey(name)) {
         System.err.println("Unable to find sound: " + name);
         System.exit(0);
         return null;
      }

      if(sound_samples.containsKey(name))
         return sound_players.get(name);

      AudioPlayer player = minim.loadFile(sound_files.get(name));
      sound_players.put(name, player);

      return player;
   }


   public void cancel_background_music(){
      for(var ap : background_musics){
         if(ap.isPlaying()){
            ap.pause();
         }
      }
      background_musics.clear();
   }


   public void start_background_music(String[] files){
      if(sys.config.music_level == 0)
         return;

      for(var f : files){
         AudioPlayer ap = get_player(f);
         ap.setGain(- (10 - sys.config.music_level) * 5);
         ap.pause();
         background_musics.add(ap);
      }

      current_background_index = new Random().nextInt(background_musics.size());
      background_musics.get(current_background_index).play();
   }


   public void update_background_music(){
      if(sys.config.music_level == 0)
         return;

      if(background_musics.get(current_background_index).isPlaying()){
         return;
      }

      // Update to the next audo file
      current_background_index = (current_background_index + 1) % background_musics.size();
      background_musics.get(current_background_index).play();
   }


   private void load_sounds(){
      System.out.println("Loading sounds");

      File sounds_folder = new File(GameEngine.SOUNDS_FOLDER);
      File[] sound_files = sounds_folder.listFiles();

      // check if empty
      if(sound_files == null){
         System.err.println(" - Failed to open sounds folder");
         return;
      }

      // Open each sound
      for(var file : sound_files){
         // Load sound
         System.out.println(" - Attempting: " + file.getName());

         String sound_name = file.getName().split("\\.")[0];

         this.sound_files.put(sound_name, file.getAbsolutePath());
      }

   }
}

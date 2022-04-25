package GameEngine;

import javazoom.jl.decoder.InputStreamSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Run the main program
        while(true) {
            Process p = Runtime.getRuntime().exec("java -jar GameEngine/game.jar");

            StreamGobbler streamGobbler =
                    new StreamGobbler(p.getInputStream(), System.out::println);

            Executors.newSingleThreadExecutor().submit(streamGobbler);

            int res = p.waitFor();
            System.out.println(res);
            if(res != 42)
                return;
        }
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }
}

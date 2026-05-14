package main;

import java.io.File;

public class SoundManager {
    private static Process musicProcess;
    private static boolean isMuted = false;
    private static final String DEFAULT_MUSIC = "backgroundsound.mp3";

    public static void playBackgroundMusic(String filePath) {
        if (isMuted) return;
        
        // If already playing, don't restart
        if (musicProcess != null && musicProcess.isAlive()) {
            return;
        }

        new Thread(() -> {
            try {
                File musicFile = new File(filePath);
                if (!musicFile.exists()) {
                    System.err.println("Music file not found: " + filePath);
                    return;
                }

                String absPath = musicFile.getAbsolutePath().replace("'", "''");
                
                // On Windows, use PowerShell to play MP3 as Java Sound API lacks native support
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    String script = "Add-Type -AssemblyName PresentationCore; " +
                                   "$player = New-Object System.Windows.Media.MediaPlayer; " +
                                   "$player.Open('" + absPath + "'); " +
                                   "$player.Play(); " +
                                   "while($true) { " +
                                   "  if($player.NaturalDuration.HasTimeSpan -and $player.Position -ge $player.NaturalDuration.TimeSpan) { " +
                                   "    $player.Position = [System.TimeSpan]::Zero; $player.Play() " +
                                   "  }; " +
                                   "  Start-Sleep 1 " +
                                   "}";

                    ProcessBuilder pb = new ProcessBuilder("powershell", "-WindowStyle", "Hidden", "-Command", script);
                    musicProcess = pb.start();
                } else {
                    System.err.println("MP3 playback is currently optimized for Windows via PowerShell fallback.");
                }
            } catch (Exception e) {
                System.err.println("Error playing music: " + e.getMessage());
            }
        }).start();
    }

    public static void stopBackgroundMusic() {
        if (musicProcess != null) {
            musicProcess.destroy();
            musicProcess = null;
        }
    }

    public static void toggleMute() {
        isMuted = !isMuted;
        if (isMuted) {
            stopBackgroundMusic();
        } else {
            playBackgroundMusic(DEFAULT_MUSIC);
        }
    }

    public static boolean isMuted() {
        return isMuted;
    }
}

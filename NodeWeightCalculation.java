import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.io.InputStreamReader;

class NodeWeightCalculation {
  //public String getOs()
  public static String matchFromFile(String file, String pattern) {
    Pattern regexp = Pattern.compile(pattern, Pattern.MULTILINE);
    Matcher matcher = regexp.matcher("");
    Path path = Paths.get(file);
    String value = "";
    try {
      BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
      LineNumberReader lineReader = new LineNumberReader(reader);

      String line = null;
      while ((line = lineReader.readLine()) != null) {
        matcher.reset(line); //reset the input
        if (matcher.find()) {
          value = line.split(":")[1];
          value = value.replaceAll("[^\\.0123456789]", "");
          break;
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return value;
  }

  public static int getWeight() {
    String os = System.getProperty("os.name").toLowerCase();
    Double cpu = 0.0;
    Integer battery = 1, state = 0;
    try {
      switch (os) {
        case "linux":
          cpu = Double.parseDouble(matchFromFile("/proc/cpuinfo", "cpu\\s+M(.*)"));
          battery = Integer.parseInt(matchFromFile("/proc/acpi/battery/BAT0/info", "design\\s+c(.*)"));
          state = Integer.parseInt(matchFromFile("/proc/acpi/battery/BAT0/state", "remaining\\s+c(.*)"));
          System.out.println(cpu + " " + battery + " " + state);
          break;
        case "windows":
          System.out.println("2");
          break;
        case "mac os x":
          //Processor Speed
          String command = "system_profiler SPPowerDataType";
          String command2 = "system_profiler SPHardwareDataType";

          Process proc = Runtime.getRuntime().exec(command);
          Process proc2 = Runtime.getRuntime().exec(command2);

          BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
          BufferedReader reader2 = new BufferedReader(new InputStreamReader(proc2.getInputStream()));

          String line = "";
          String value;
          while ((line = reader.readLine()) != null) {
            if (line.contains("Charge")) {
              if (line.contains("Charge Remaining")) {
                value = line.split(":")[1];
                value = value.replaceAll("[^\\.0123456789]", "");
                state = Integer.parseInt(value);
              } else if (line.contains("Charge Capacity")) {
                value = line.split(":")[1];
                value = value.replaceAll("[^\\.0123456789]", "");
                battery = Integer.parseInt(value);
              }
            }
          }
          while ((line = reader2.readLine()) != null) {
            if (line.contains("Processor Speed")) {
              value = line.split(":")[1];
              value = value.replaceAll("[^\\.0123456789]", "");
              cpu = 1000 * Double.parseDouble(value);
            }
          }
          System.out.println(cpu + " " + battery + " " + state);
          System.out.println("3");
          break;
        case "android":
          System.out.println("4");
          break;
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return (int)(cpu + (state * 1000) / battery);
  }
}
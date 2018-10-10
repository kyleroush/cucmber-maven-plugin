package kyle;

import io.cucumber.gherkin.Gherkin;
import io.cucumber.messages.Messages;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetUp {


  public Map<String, Messages.Feature> buildTagFeatureMap(Map<String, File> files) {
    Map<String, Messages.Feature> extraFiles = new HashMap<>();
    for (String e: files.keySet()) {
      Messages.Wrapper w = Gherkin
          .fromPaths(Collections.singletonList(files.get(e).getPath()),  false, true, false).get(0);
      extraFiles.put(e, w.getGherkinDocument().getFeature());
    }
    return extraFiles;
  }

  public Map<String, File> buildTagMap(List<File> files) {
    Map<String, File> extraFiles = new HashMap<>();
    for (File file: files) {

      if(file.isDirectory()) {
        extraFiles.putAll(buildTagMap(Arrays.asList(file.listFiles())));
      } else {

        // build a key that would look like the annotation
        // so instead someFeature.feature
        // it would be @someFeature
        String key = "@" + file.getName().replace(".feature", "");
        extraFiles.put(key, file);
      }
    }
    return extraFiles;
  }

  /**
   * recusivily search the files provided to collect all the files locations from the nested directories.
   * @param files
   * @return
   */
  public List<String> buildFeatureList(List<File> files) {
    List<String> fileNames = new ArrayList<>();
    for(File file: files) {
      if(file.isDirectory()) {
        fileNames.addAll(buildFeatureList(Arrays.asList(file.listFiles())));
      } else {
        fileNames.add(file.toString());
      }
    }
    return fileNames;
  }
}

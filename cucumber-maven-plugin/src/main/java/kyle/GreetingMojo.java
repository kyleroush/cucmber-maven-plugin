package kyle;

import io.cucumber.gherkin.Gherkin;
import io.cucumber.messages.Messages.Tag;
import io.cucumber.messages.Messages.Wrapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Says "Hi" to the user.
 */
@Mojo( name = "sayhi")
public class GreetingMojo extends AbstractMojo {

    @Parameter
    File outputDirectory;

    @Parameter
    List<File> coreFeatureFiles;

    @Parameter
    List<File> extraFeatureFiles;

    public void execute() throws MojoExecutionException {

        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        //map extraFeatureFiles
        Map<String, File> extraFiles = buildTagMap(extraFeatureFiles);


        // if given a directory search it
        List<String> fileNames = buildFeatureList(coreFeatureFiles);

        for (Wrapper wrapper: Gherkin.fromPaths(fileNames,  false, true, false)) {
            getLog().info("name");

            String uri = wrapper.getGherkinDocumentOrBuilder().getUri();
            getLog().info(uri);

            getLog().info(uri.substring(uri.lastIndexOf('/')));

            File newFile = new File(outputDirectory, uri.substring(uri.lastIndexOf('/')));

            append(newFile, new File(wrapper.getGherkinDocumentOrBuilder().getUri()));

            for(Tag tag: wrapper.getGherkinDocument().getFeature().getTagsList()) {
                getLog().info(tag.getName().toString());
                if (extraFiles.containsKey(tag.getName())) {
                    getLog().info("found tag " +tag.getName());
                    append(newFile, extraFiles.get(tag.getName()));

                }
            }
        }
    }



    public void append(File fileToWriteTo, File fileToReadFrom) throws MojoExecutionException {
        try(
                FileWriter fw = new FileWriter(fileToWriteTo, true);
                FileReader fr = new FileReader(fileToReadFrom)
        ) {
            int c = fr.read();
            while(c!=-1) {
                fw.write(c);
                c = fr.read();
            }
            fw.write("\r\n");
            //w.write(new File(wrapper.getGherkinDocumentOrBuilder().getUri()).rea);
        }
        catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + fileToWriteTo, e);
        }
    }


    public Map<String, File> buildTagMap(List<File> files) {
        Map<String, File> extraFiles = new HashMap<>();
        for (File file: extraFeatureFiles) {

            // build a key that would look like the annotation
            // so instead someFeature.feature
            // it would be @someFeature
            String key = "@" + file.getName().replace(".feature", "");

            extraFiles.put(key, file);
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
        for(File file: coreFeatureFiles) {
            if(file.isDirectory()) {
                buildFeatureList(Arrays.asList(file.listFiles()));
            } else {
                fileNames.add(file.toString());
            }
        }
        return fileNames;
    }

}

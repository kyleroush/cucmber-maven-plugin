package kyle;

import io.cucumber.gherkin.Gherkin;
import io.cucumber.messages.Messages;
import io.cucumber.messages.Messages.Tag;
import io.cucumber.messages.Messages.Wrapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
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

    @Inject
    SetUp setUp;
    @Inject
    Writer writer;
    @Inject
    BuildFeatures buildFeatures;

    public void execute() throws MojoExecutionException {

        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        //SetUp setUp = new SetUp();

        //map extraFeatureFiles
        Map<String, File> extraFiles = setUp.buildTagMap(extraFeatureFiles);

        Map<String, Messages.Feature> extraFeatures = setUp.buildTagFeatureMap(extraFiles);

        getLog().info(extraFeatures.keySet().toString());

        // if given a directory search it
        List<String> fileNames = setUp.buildFeatureList(coreFeatureFiles);
        getLog().info(fileNames.toString());

        for (String name : fileNames) {
            getLog().info("name "+name);

            for (Wrapper wrapper : Gherkin.fromPaths(Collections.singletonList(name), false, true, false)) {
                getLog().info("parsed");

                Messages.Feature feature = buildFeatures.buildFeatures(wrapper, extraFeatures);

                String uri = wrapper.getGherkinDocumentOrBuilder().getUri();
                getLog().info(uri);
                getLog().info(uri.substring(uri.lastIndexOf('/')));
                File newFile = new File(outputDirectory, uri.substring(uri.lastIndexOf('/')));

                writer.write(feature, newFile);
            }
        }
    }


}

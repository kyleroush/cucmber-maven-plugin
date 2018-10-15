package kyle;

import io.cucumber.gherkin.Gherkin;
import io.cucumber.messages.Messages;
import io.cucumber.messages.Messages.Wrapper;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Generate feature files for more comprehensive and reusable files
 *
 * To debug and log getLog().info("value");
 */
@Mojo( name = "generate")
public class CucumberMavenPlugin extends AbstractMojo {

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

    /**
     * This is where the plugin starts.
     *
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {

        // create the out put directory
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        //map extraFeatureFiles
        Map<String, File> extraFiles = setUp.buildTagMap(extraFeatureFiles);

        // convert the extraFiles to extraFeatures
        Map<String, Messages.Feature> extraFeatures = setUp.buildTagFeatureMap(extraFiles);

        // collect all the core feature
        List<String> fileNames = setUp.buildFeatureList(coreFeatureFiles);

        for (String name : fileNames) {
            //parse the core feature
            Wrapper wrapper = Gherkin.fromPaths(Collections.singletonList(name), false, true, false).get(0);

            // build the new feature
            Messages.Feature feature = buildFeatures.buildFeatures(wrapper.getGherkinDocument().getFeature(), extraFeatures);

            // create new file in output dir
            String uri = wrapper.getGherkinDocumentOrBuilder().getUri();
            File newFile = new File(outputDirectory, uri.substring(uri.lastIndexOf('/')));

            // write the new file
            writer.write(feature, newFile);
        }
    }
}

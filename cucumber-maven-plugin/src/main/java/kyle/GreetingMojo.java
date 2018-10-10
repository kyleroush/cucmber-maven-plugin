package kyle;

import io.cucumber.gherkin.Gherkin;
import io.cucumber.messages.Messages;
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
import java.util.Set;
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

        Map<String, Messages.Feature> extraFeatures = buildTagFeatureMap(extraFiles);

        getLog().info(extraFeatures.keySet().toString());

            // if given a directory search it
        List<String> fileNames = buildFeatureList(coreFeatureFiles);

        for (Wrapper wrapper: Gherkin.fromPaths(fileNames,  false, true, false)) {
            getLog().info("name");


            Messages.Feature.Builder builder = wrapper.getGherkinDocument().getFeature().toBuilder();

            String uri = wrapper.getGherkinDocumentOrBuilder().getUri();
            getLog().info(uri);

            getLog().info(uri.substring(uri.lastIndexOf('/')));

            File newFile = new File(outputDirectory, uri.substring(uri.lastIndexOf('/')));

            //append(newFile, new File(wrapper.getGherkinDocumentOrBuilder().getUri()));

            List<Tag> tags = new ArrayList<>(wrapper.getGherkinDocument().getFeature().getTagsList());
            getLog().info("init " + tags);

            for (int i = 0; i < tags.size(); i++) {
                Tag tag = tags.get(i);
                getLog().info("key to look up for " + tag.getName());
                getLog().info("keys" + extraFeatures.keySet().toString());
                if (extraFeatures.containsKey(tag.getName())) {
                    getLog().info("found tag " +tag.getName());
                    Messages.Feature extrafeatrue = extraFeatures.get(tag.getName());

                    List<Tag> extratags = new ArrayList<>(extrafeatrue.getTagsList());
                    extratags.removeAll(tags);
                    builder.addAllTags(extratags);
                    tags.addAll(extratags);
                    getLog().info("new tags " +tag.getName());




                    builder.addAllChildren(newScenarios(extrafeatrue.getChildrenList()));
                }
            }

            try(FileWriter fw = new FileWriter(newFile)) {
                fw.write(writeFeature(builder.build()));
            } catch (IOException e) {
                throw new MojoExecutionException("Error creating file ", e);
            }
        }
    }



    public String writeFeature(Messages.Feature feature) {
        String raw = "";

        for(Tag t :feature.getTagsList()){
            raw+=t.getName();
            raw+='\n';
        }

        raw+=feature.getKeyword() +": "+ feature.getName();
        raw+='\n';
        raw += '\n';


        for(Messages.FeatureChild child :feature.getChildrenList()) {
            raw += '\n';

            //output the back ground
            if(child.hasBackground()) {

                Messages.Background b= child.getBackground();

                raw += "\t";

                raw += b.getKeyword()+": "+b.getName();
                raw += '\n';
                raw = writeSteps(raw, b.getStepsList());
            }

            //out the scenario
            if(child.hasScenario()) {
                Messages.Scenario s= child.getScenario();


                for(Tag t :s.getTagsList()){
                    raw += "\t";
                    raw+=t.getName();
                    raw+='\n';
                }

                raw += "\t";
                raw += s.getKeyword()+": "+s.getName();
                raw += '\n';
                raw = writeSteps(raw, s.getStepsList());


                for(Messages.Examples examlpe :s.getExamplesList()){
                    raw += '\n';
                    for(Tag t :examlpe.getTagsList()){
                        raw += "\t";
                        raw+=t.getName();
                        raw+='\n';
                    }
                    raw += "\t";
                    raw += examlpe.getKeyword()+": "+examlpe.getName();
                    raw+='\n';

                    raw += "\t\t";

                    raw+='|';

                    for(Messages.TableCell cell: examlpe.getTableHeader().getCellsList()){
                        raw+=cell.getValue();
                        raw+='|';

                    }
                    raw+='\n';
                    raw = writeTable(raw, examlpe.getTableBodyList(), "\t\t");
                }

            }
            raw += '\n';

        }

        //getLog().info(raw);
        return raw;
    }

    public String writeSteps(String raw, List<Messages.Step> steps) {
        for(Messages.Step step :steps) {
            raw += "\t\t";
            raw += step.getKeyword()+step.getText();
            raw += '\n';

            if(step.hasDocString()) {
                raw += step.getDocString().getDelimiter();
                raw += '\n';
                raw += step.getDocString().getContent();
                raw += '\n';
                raw += step.getDocString().getDelimiter();
                raw += '\n';
            }
            if(step.hasDataTable()) {
                raw = writeTable(raw, step.getDataTable().getRowsList(), "\t\t\t");
            }
        }


        return raw;
    }

    public String writeTable(String raw, List<Messages.TableRow> example, String tabs) {


        for(Messages.TableRow t: example){
            raw += tabs;

            raw+='|';

            for(Messages.TableCell cell: t.getCellsList()){
                raw+=cell.getValue();
                raw+='|';

            }
            raw+='\n';
        }
        return raw;
    }


    public Map<String, Messages.Feature> buildTagFeatureMap(Map<String, File> files) {
        Map<String, Messages.Feature> extraFiles = new HashMap<>();
        for (String e: files.keySet()) {
            Wrapper w = Gherkin.fromPaths(Collections.singletonList(files.get(e).getPath()),  false, true, false).get(0);
            extraFiles.put(e, w.getGherkinDocument().getFeature());
        }
        return extraFiles;
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

    public List<Messages.FeatureChild> newScenarios( List<Messages.FeatureChild> children) {

        Messages.Background background = null;
        for (Messages.FeatureChild child:children) {
                if (child.hasBackground()){
                    background = child.getBackground();
                }
        }

        if(background != null){
            List<Messages.FeatureChild> newChildren = new ArrayList<>();


            for (Messages.FeatureChild child:children) {
                if (child.hasScenario()){

                    List<Messages.Step> steps = new ArrayList<>(background.getStepsList());
                    steps.addAll(new ArrayList<>(child.getScenario().getStepsList()));

                    newChildren.add(child.toBuilder().setScenario(child.getScenario().toBuilder().clearSteps().addAllSteps(steps).build()).build());
                }
            }

            return newChildren;
        }


        return children;
    }

}

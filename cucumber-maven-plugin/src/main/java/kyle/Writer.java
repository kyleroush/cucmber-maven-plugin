package kyle;

import io.cucumber.messages.Messages;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Take a file and feature and write them out.
 */
public class Writer {


  public void write(Messages.Feature feature, File newFile) throws MojoExecutionException {
    try(FileWriter fw = new FileWriter(newFile)) {
      fw.write(new Writer().writeFeature(feature));
    } catch (IOException e) {
      throw new MojoExecutionException("Error writing file", e);
    }
  }

  private String writeFeature(Messages.Feature feature) {
    String raw = "";

    for(Messages.Tag t :feature.getTagsList()){
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


        for(Messages.Tag t :s.getTagsList()){
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
          for(Messages.Tag t :examlpe.getTagsList()){
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

    return raw;
  }

  private String writeSteps(String raw, List<Messages.Step> steps) {
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

  private String writeTable(String raw, List<Messages.TableRow> example, String tabs) {

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
}

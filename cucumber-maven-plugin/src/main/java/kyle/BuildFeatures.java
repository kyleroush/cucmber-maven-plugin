package kyle;

import io.cucumber.messages.Messages;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BuildFeatures {


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


  public  Messages.Feature buildFeatures(Messages.Wrapper wrapper, Map<String, Messages.Feature> extraFeatures) {
    Messages.Feature.Builder builder =
        wrapper.getGherkinDocument().getFeature().toBuilder();
    List<Messages.Tag> tags =
        new ArrayList<>(wrapper.getGherkinDocument().getFeature().getTagsList());
    //getLog().info("init " + tags);

    for (int i = 0; i < tags.size(); i++) {
      Messages.Tag tag = tags.get(i);
      //getLog().info("key to look up for " + tag.getName());
      //getLog().info("keys" + extraFeatures.keySet().toString());
      if (extraFeatures.containsKey(tag.getName())) {
        //getLog().info("found tag " + tag.getName());
        Messages.Feature extrafeatrue = extraFeatures.get(tag.getName());

        List<Messages.Tag> extratags = new ArrayList<>(extrafeatrue.getTagsList());
        extratags.removeAll(tags);
        builder.addAllTags(extratags);
        tags.addAll(extratags);
        //getLog().info("new tags " + tag.getName());

        builder.addAllChildren(newScenarios(extrafeatrue.getChildrenList()));
      }
    }
    return builder.build();
  }
}

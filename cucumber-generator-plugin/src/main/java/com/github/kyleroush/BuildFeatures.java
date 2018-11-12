package com.github.kyleroush;

import io.cucumber.messages.Messages;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Takes in a parent feature and a map of sub features and combine them to build a new feature.
 */
public class BuildFeatures {

  /**
   * Gets all the scenarios from a feature and prepend the background of that feature so it is not lost
   *
   * @param children the children of a feature (the scenarios and background)
   * @return the feature children that are generate with background included in the scenarios
   */
  private List<Messages.FeatureChild> newScenarios(List<Messages.FeatureChild> children) {

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

  /**
   * Build a feature by combining all features that match to tags in the parent feature
   */
  public  Messages.Feature buildFeatures(Messages.Feature feature, Map<String, Messages.Feature> extraFeatures) {
    Messages.Feature.Builder builder = feature.toBuilder();
    List<Messages.Tag> tags =
        new ArrayList<>(feature.getTagsList());
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

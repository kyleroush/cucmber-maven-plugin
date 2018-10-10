# cucmber-maven-plugin
## What it does
Lets you seperate feature into into core features and extra feature. Where the extra features can be reused in the core features.
1) The core features are the main features that can have many extra features added to it
2) The extra features are the features that are not to be run by its self and are to be added to ont to many core features

This is about how you can have common senarios across multiple feature file that you want to have managed in a single places but applied to mutltiple features
so what this does is given feature files it will check the tags on the feature and see if you have specified a subfeature that matches that tag and it will generate a new file with the all additional steps from all of the sub features.


## How to use it




## configuration
* coreFeatureFiles List of files/directories of the core features that you want to add to
* extraFeatureFiles List of files/directories of the extra features
* outputDirectory The location of the ouputed feature files

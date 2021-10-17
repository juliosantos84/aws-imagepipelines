package com.everythingbiig.aws.imagepipelines;

import java.util.List;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.StackProps;

public class EtherythingbiigImagePipeline extends ImagePipelineBase {

    public EtherythingbiigImagePipeline(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public EtherythingbiigImagePipeline(final Construct scope, final String id, StackProps props) {
        super(scope, id, props);
    }

    protected String getAmiName() {
        return (String) super.getNode()
            .tryGetContext("everythingbiig-aws-imagepipelines/etherythingbiig:amiName");
    }


    protected String getRecipeVersion() {
        return (String) super.getNode()
            .tryGetContext("everythingbiig-aws-imagepipelines/etherythingbiig:recipeVersion");
    }

    @SuppressWarnings("unchecked")
    protected List<String> getDistributionRegions() {
        return (List<String>) super.getNode()
            .tryGetContext("everythingbiig-aws-imagepipelines/etherythingbiig:distributionRegions");
    }

    @Override
    protected String getPipelineName() {
        return "etherythingbiig";
    }
}

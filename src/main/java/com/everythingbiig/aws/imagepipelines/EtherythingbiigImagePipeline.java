package com.everythingbiig.aws.imagepipelines;

import java.util.HashMap;
import java.util.List;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.s3.assets.Asset;

public class EtherythingbiigImagePipeline extends ImagePipelineBase {

    private Asset scriptsAsset = null;
    private Asset unitsAsset = null;

    public EtherythingbiigImagePipeline(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public EtherythingbiigImagePipeline(final Construct scope, final String id, StackProps props) {
        super(scope, id, props);
        // getScriptsAsset();
        getUnitsAsset();
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

    protected Asset getScriptsAsset() {
        if (this.scriptsAsset == null) {
            this.scriptsAsset = getAsset("scriptsAsset", "/assets/etherythingbiig/scripts");
            this.scriptsAsset.grantRead(getImageBuilderRoleArn());
        }
        return this.scriptsAsset;
    }

    protected Asset getUnitsAsset() {
        if (this.unitsAsset == null) {
            this.unitsAsset = getAsset("unitsAsset", "/assets/etherythingbiig/units");
            this.unitsAsset.grantRead(getImageBuilderRoleArn());
        }
        return this.unitsAsset;
    }

    @Override
    protected ComponentHelper getComponentHelper() {
        ComponentHelper componentHelper = new ComponentHelper(this, "/imagebuilder/etherythingbiig/components");
        componentHelper.setParameters(new HashMap<String, String>(){
            {
                put("SCRIPTS_S3_URL", getScriptsAsset().getS3ObjectUrl());
                put("SCRIPTS_S3_BUCKET_NAME", getScriptsAsset().getS3BucketName());
                put("SCRIPTS_S3_OBJECT_KEY", getScriptsAsset().getS3ObjectKey());
            }
        });
        return componentHelper;
    }
}

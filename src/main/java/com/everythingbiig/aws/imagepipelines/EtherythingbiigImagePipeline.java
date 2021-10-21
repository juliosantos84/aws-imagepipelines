package com.everythingbiig.aws.imagepipelines;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.LookupMachineImage;
import software.amazon.awscdk.services.s3.assets.Asset;

public class EtherythingbiigImagePipeline extends AbstractImagePipeline {

    private Asset scriptsAsset = null;
    private Asset servicesAsset = null;

    public EtherythingbiigImagePipeline(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public EtherythingbiigImagePipeline(final Construct scope, final String id, StackProps props) {
        super(scope, id, props);
    }

    @Override
    protected String getAmiName() {
        return (String) super.getNode()
            .tryGetContext("everythingbiig-aws-imagepipelines/etherythingbiig:amiName");
    }

    @Override
    protected String getRecipeVersion() {
        return (String) super.getNode()
            .tryGetContext("everythingbiig-aws-imagepipelines/etherythingbiig:recipeVersion");
    }

    @Override
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

    protected Asset getServicesAsset() {
        if (this.servicesAsset == null) {
            this.servicesAsset = getAsset("servicesAsset", "/assets/etherythingbiig/services");
            this.servicesAsset.grantRead(getImageBuilderRoleArn());
        }
        return this.servicesAsset;
    }

    /**
     * aws ec2 describe-images 
     * --owners 099720109477 
     * --filters "Name=name,Values=ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-20211015"
     */
    @Override
    protected String getParentImage() {
        return LookupMachineImage.Builder.create()
            .owners(Arrays.asList("099720109477"))
            .filters(new HashMap<String,List<String>>() {
                {
                    put("name", Arrays.asList("ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-20211015"));
                }
            })
            .name("ubuntuFocal2004Lookup")
            .build()
                .getImage(this)
                    .getImageId();
    }

    @Override
    protected ComponentHelper getComponentHelper() {
        ComponentHelper componentHelper = new ComponentHelper(this, "/imagebuilder/etherythingbiig/components");
        componentHelper.setParameters(new HashMap<String, String>(){
            {
                put("SCRIPTS_S3_URL", getScriptsAsset().getS3ObjectUrl());
                put("SERVICES_S3_URL", getServicesAsset().getS3ObjectUrl());
            }
        });
        return componentHelper;
    }
}

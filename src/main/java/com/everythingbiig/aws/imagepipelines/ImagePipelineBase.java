package com.everythingbiig.aws.imagepipelines;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.text.CaseUtils;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.LookupMachineImage;
import software.amazon.awscdk.services.iam.IRole;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.imagebuilder.CfnDistributionConfiguration;
import software.amazon.awscdk.services.imagebuilder.CfnDistributionConfiguration.DistributionProperty;
import software.amazon.awscdk.services.imagebuilder.CfnImagePipeline;
import software.amazon.awscdk.services.imagebuilder.CfnImageRecipe;
import software.amazon.awscdk.services.imagebuilder.CfnInfrastructureConfiguration;
import software.amazon.awscdk.services.s3.assets.Asset;

/**
 * Provisions and tests the etherythingbiig AMI used to run
 * a self-contained beacon chain node.
 */
public abstract class ImagePipelineBase extends Stack {

    private CfnImagePipeline pipeline = null;
    private IRole imageBuilderRole = null;

    public ImagePipelineBase(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ImagePipelineBase(final Construct scope, final String id, StackProps props) {
        super(scope, id, props);
        getImagePipeline();
    }

    /**
     * aws ec2 describe-images 
     *  --owners amazon 
     *  --filters "Name=name,Values=amzn2-ami-hvm-2.0.20211001.1-x86_64-ebs"
     * @return
     */
    protected String getParentImage() {
        return LookupMachineImage.Builder.create()
            .owners(Arrays.asList("amazon"))
            .filters(new HashMap<String,List<String>>() {
                {
                    put("name", Arrays.asList("amzn2-ami-hvm-2.0.20211001.1-x86_64-ebs"));
                }
            })
            .name("amazonLinux2AmiLookup")
            .build()
                .getImage(this)
                    .getImageId();
    }

    protected abstract String getAmiName();

    protected abstract String getRecipeVersion();

    protected abstract List<String> getDistributionRegions();

    protected abstract String getPipelineName();

    protected abstract ComponentHelper getComponentHelper();

    protected List<DistributionProperty> getDistributionPropertyList() {
        List<DistributionProperty> distroProps = new ArrayList<DistributionProperty>();
        List<String> distroRegions = getDistributionRegions();
        if (distroRegions == null) {
            throw new IllegalArgumentException("distributionRegions cannot be null");
        }
        for (String distroRegion : distroRegions) {
            distroProps.add(
                DistributionProperty.builder()
                    .amiDistributionConfiguration(new HashMap<String, Object>(){
                        {
                            put("name", ImagePipelineBase.this.getAmiName());
                        }
                    })
                    .region(distroRegion)
                    .build()
            );
        }
        return distroProps;
    }

    protected String getScopePrefixedId(String id) {
        return CaseUtils.toCamelCase(String.format("%s-%s",this.getPipelineName(), id), false, '-');
    }

    protected CfnImagePipeline getImagePipeline() {

        if (pipeline == null) {

            CfnDistributionConfiguration distroConfig = CfnDistributionConfiguration.Builder
                .create(this, getScopePrefixedId("distroConfig"))
                .name(getScopePrefixedId("distroConfig"))
                .description("Distribution Config.")
                .distributions(getDistributionPropertyList())
                .build();

            ComponentHelper componentHelper = getComponentHelper();

            CfnImageRecipe recipe = CfnImageRecipe.Builder.create(this, "imageRecipe")
                .name(getScopePrefixedId("imageRecipe"))
                // amzn2-ami-hvm-2.0.20211001.1-x86_64-ebs
                .parentImage(getParentImage())
                .description("Image Recipe")
                .workingDirectory("/tmp")
                .version(getRecipeVersion())
                .components(componentHelper.getComponentConfigurationProperties(this))
                .build();

            CfnInfrastructureConfiguration infraConfig = 
                CfnInfrastructureConfiguration.Builder.create(this, getScopePrefixedId("infraConfig"))
                    .name(getScopePrefixedId("infraConfig"))
                    .description("Infrastructure Config")
                    .terminateInstanceOnFailure(Boolean.TRUE)
                    .instanceProfileName("EC2InstanceProfileForImageBuilder")
                    .build();

            pipeline = CfnImagePipeline.Builder
                .create(this, getScopePrefixedId("imagePipeline"))
                .name(getScopePrefixedId("imagePipeline"))
                .description("Image Pipeline")
                .distributionConfigurationArn(distroConfig.getAttrArn())
                .imageRecipeArn(recipe.getAttrArn())
                .infrastructureConfigurationArn(infraConfig.getAttrArn())
                .build();
        }
        return pipeline;
    }
    
    protected Asset getAsset(String assetId, String localAssetPath) {
        Asset asset = null;
        try {
            File filePath = Paths.get(ImagePipelineBase.class.getResource(localAssetPath).toURI())
                .toFile();
            asset = Asset.Builder.create(this, assetId)
                .path(filePath.toString())
                .build();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            //TODO handle errors
            asset = null;
        }
        return asset;
    }

    protected IRole getImageBuilderRoleArn() {
        if(this.imageBuilderRole == null) {
            this.imageBuilderRole = Role.fromRoleArn(this, getScopePrefixedId("imageBuilderRoleArn"), String.format("arn:aws:iam::%s:role/EC2InstanceProfileForImageBuilder", this.getAccount()));
        }
        return this.imageBuilderRole;
    }
}

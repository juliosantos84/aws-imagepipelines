package com.everythingbiig.aws.imagepipelines;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.LookupMachineImage;
import software.amazon.awscdk.services.imagebuilder.CfnDistributionConfiguration;
import software.amazon.awscdk.services.imagebuilder.CfnDistributionConfiguration.DistributionProperty;
import software.amazon.awscdk.services.imagebuilder.CfnImagePipeline;
import software.amazon.awscdk.services.imagebuilder.CfnImageRecipe;
import software.amazon.awscdk.services.imagebuilder.CfnInfrastructureConfiguration;

/**
 * Provisions and tests the etherythingbiig AMI used to run
 * a self-contained beacon chain node.
 */
public class EtherythingbiigImagePipeline extends Stack {

    private CfnImagePipeline pipeline = null;

    public EtherythingbiigImagePipeline(final Construct scope, final String id) {
        this(scope, id, null);
    }
    public EtherythingbiigImagePipeline(final Construct scope, final String id, StackProps props) {
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

    protected String getAmiName() {
        return (String) this.getNode()
            .tryGetContext("everythingbiig-aws-imagepipelines/etherythingbiig:amiName");
    }

    protected String getRecipeVersion() {
        return (String) this.getNode()
            .tryGetContext("everythingbiig-aws-imagepipelines/etherythingbiig:recipeVersion");
    }

    public CfnImagePipeline getImagePipeline() {

        if (pipeline == null) {

            CfnDistributionConfiguration distroConfig = CfnDistributionConfiguration.Builder
                .create(this, "etherythingbiigDistroConfig")
                .name("etherythingbiigDistroConfig")
                .description("Distributes to us-east-2.")
                .distributions(Arrays.asList(
                    DistributionProperty.builder()
                        .amiDistributionConfiguration(new HashMap<String, Object>(){
                            {
                                put("name", EtherythingbiigImagePipeline.this.getAmiName());
                            }
                        })
                        .region(EtherythingbiigImagePipeline.this.getRegion())
                        .build()))
                .build();

            ComponentHelper componentHelper = new ComponentHelper(this, "/imagebuilder/etherythingbiig/components");

            CfnImageRecipe recipe = CfnImageRecipe.Builder.create(this, "imageRecipe")
                .name("etherythingbiigImageRecipe")
                // amzn2-ami-hvm-2.0.20211001.1-x86_64-ebs
                .parentImage(getParentImage())
                .description("Etherythingbiig Image Recipe")
                .workingDirectory("/tmp")
                .version(getRecipeVersion())
                .components(componentHelper.getComponentConfigurationProperties(this))
                .build();
            
            CfnInfrastructureConfiguration infraConfig = 
                CfnInfrastructureConfiguration.Builder.create(this, "etherythingbiigInfraConfig")
                    .name("etherythingbiigInfraConfig")
                    .description("Etherythingbiig Infrastructure Config")
                    .terminateInstanceOnFailure(Boolean.TRUE)
                    .instanceProfileName("EC2InstanceProfileForImageBuilder")
                    .build();

            pipeline = CfnImagePipeline.Builder
                .create(this, "etherythingbiigImagePipeline")
                .name("etherythingbiigImagePipeline")
                .description("Etherythingbiig Image Pipeline")
                .distributionConfigurationArn(distroConfig.getAttrArn())
                .imageRecipeArn(recipe.getAttrArn())
                .infrastructureConfigurationArn(infraConfig.getAttrArn())
                .build();
        }
        return pipeline;
    }
}

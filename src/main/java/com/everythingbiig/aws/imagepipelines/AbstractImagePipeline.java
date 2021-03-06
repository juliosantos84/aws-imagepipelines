package com.everythingbiig.aws.imagepipelines;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.text.CaseUtils;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.LookupMachineImage;
import software.amazon.awscdk.services.iam.CfnInstanceProfile;
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
public abstract class AbstractImagePipeline extends Stack {

    private CfnImagePipeline pipeline = null;
    private IRole imageBuilderRole = null;
    private CfnInstanceProfile imagebuilderInstanceProfile = null;

    public AbstractImagePipeline(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AbstractImagePipeline(final Construct scope, final String id, StackProps props) {
        super(scope, id, props);
        createImagebuilderRole();
        createImagePipeline();
    }

    /**
     * aws ec2 describe-images 
     *  --owners amazon 
     *  --filters "Name=name,Values=amzn2-ami-hvm-2.0.20211001.1-x86_64-ebs"
     * @return
     */
    protected String getParentImage() {
        List<String> parentImageNames = (List<String>) getNode()
            .tryGetContext("everythingbiig-aws-imagepipelines/parentImageNames");
        List<String> parentImageOwners = (List<String>) getNode()
            .tryGetContext("everythingbiig-aws-imagepipelines/parentImageOwners");
        return LookupMachineImage.Builder.create()
            .owners(parentImageOwners)
            .filters(new HashMap<String,List<String>>() {
                {
                    put("name", parentImageNames);
                }
            })
            .name("defaultParentImageLookup")
            .build()
                .getImage(this)
                    .getImageId();
    }

    protected abstract String getAmiName();

    protected abstract String getRecipeVersion();

    protected abstract List<String> getDistributionRegions();

    protected abstract String getPipelineName();

    protected ComponentHelper getComponentHelper(){
        return new ComponentHelper(this, "/imagebuilder/default/components");
    }

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
                            put("name", AbstractImagePipeline.this.getAmiName());
                        }
                    })
                    .region(distroRegion)
                    .build()
            );
        }
        return distroProps;
    }

    protected String getScopedId(String id) {
        return CaseUtils.toCamelCase(String.format("%s-%s",this.getPipelineName(), id), false, '-');
    }

    protected void createImagePipeline() {
        CfnDistributionConfiguration distroConfig = CfnDistributionConfiguration.Builder
            .create(this, getScopedId("DistroConfig"))
            .name(getScopedId("DistroConfig"))
            .description("Distribution Config.")
            .distributions(getDistributionPropertyList())
            .build();

        ComponentHelper componentHelper = getComponentHelper();

        CfnImageRecipe recipe = CfnImageRecipe.Builder.create(this, "imageRecipe")
            .name(getScopedId("ImageRecipe"))
            // amzn2-ami-hvm-2.0.20211001.1-x86_64-ebs
            .parentImage(getParentImage())
            .description("Image Recipe")
            .workingDirectory("/tmp")
            .version(getRecipeVersion())
            .components(componentHelper.getComponentConfigurationProperties(this))
            .build();
        
        CfnInfrastructureConfiguration infraConfig = 
            CfnInfrastructureConfiguration.Builder.create(this, getScopedId("InfraConfig"))
                .name(getScopedId("InfraConfig"))
                .description("Infrastructure Config")
                .terminateInstanceOnFailure(Boolean.TRUE)
                .instanceProfileName(getImagebuilderRole().getRoleName())
                .build();

        pipeline = CfnImagePipeline.Builder
            .create(this, getScopedId("ImagePipeline"))
            .name(getScopedId("ImagePipeline"))
            .description("Image Pipeline")
            .distributionConfigurationArn(distroConfig.getAttrArn())
            .imageRecipeArn(recipe.getAttrArn())
            .infrastructureConfigurationArn(infraConfig.getAttrArn())
            .build();
    }

    protected Asset getAsset(String assetId, String localAssetPath) {
        Asset asset = null;
        try {
            File filePath = Paths.get(AbstractImagePipeline.class.getResource(localAssetPath).toURI())
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

    protected void createImagebuilderRole() {
        this.imageBuilderRole = Role.fromRoleArn(this, "imagebuilderEc2Role", 
            String.format("arn:aws:iam::%s:role/%s", getAccount(), "EC2RoleForImageBuilder"));
    }

    protected IRole getImagebuilderRole() {
        return this.imageBuilderRole;
    }
}

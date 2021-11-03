package com.everythingbiig.aws.imagepipelines;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.StackProps;

public final class ImagepipelineApp {
    public static void main(final String[] args) {
        App app = new App();

        new EtherythingbiigImagePipeline(app, "etherythingbiigImagePipeline", 
            StackProps.builder()
                .env(getDeployEnvironment())
                .build());

        app.synth();
    }

    protected static Environment getDeployEnvironment() {
        String account = System.getenv("CDK_DEPLOY_ACCOUNT");
        if (account == null || account.trim().length() == 0) {
            account = System.getenv("CDK_DEFAULT_ACCOUNT");
            System.out.println(String.format("Falling back to CDK_DEFAULT_ACCOUNT=%s", account));
        }
        String region = System.getenv("CDK_DEPLOY_REGION");
        if (region == null || region.trim().length() == 0) {
            region = System.getenv("CDK_DEFAULT_REGION");
            System.out.println(String.format("Falling back to CDK_DEFAULT_REGION=%s", region));
        }
        return Environment.builder()
                .account(account)
                .region(region)
                .build();
    }
}

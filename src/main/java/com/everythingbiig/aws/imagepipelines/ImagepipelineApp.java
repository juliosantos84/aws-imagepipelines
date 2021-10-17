package com.everythingbiig.aws.imagepipelines;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.StackProps;

public final class ImagepipelineApp {
    public static void main(final String[] args) {
        App app = new App();

        new EtherythingbiigImagePipeline(app, "ImagepipelineStack", 
            StackProps.builder()
                .env(Environment.builder()
                    .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
                    .region(System.getenv("CDK_DEFAULT_REGION"))
                    .build())
                .build());
        app.synth();
    }
}

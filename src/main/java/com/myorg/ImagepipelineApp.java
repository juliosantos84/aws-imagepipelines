package com.myorg;

import software.amazon.awscdk.core.App;

public final class ImagepipelineApp {
    public static void main(final String[] args) {
        App app = new App();

        new ImagepipelineStack(app, "ImagepipelineStack");

        app.synth();
    }
}

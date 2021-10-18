package com.everythingbiig.aws.imagepipelines;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.CaseUtils;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.services.imagebuilder.CfnComponent;
import software.amazon.awscdk.services.imagebuilder.CfnImageRecipe;

public class ComponentHelper {

    public static final String COMPONENT_RESOURCE_PATH_TEMPLATE = "/imagebuilder/components";

    private String componentResourcePath = null;

    private Construct parentScope = null;

    private List<CfnComponent> components  = null;

    private Map<String, String> parameters = null;

    public ComponentHelper (Construct parentScope) {
        this(parentScope, COMPONENT_RESOURCE_PATH_TEMPLATE);
    }

    public ComponentHelper (Construct parentScope, String componentResourcePath) {
        if (parentScope == null || componentResourcePath == null || componentResourcePath.trim().length() == 0) {
            throw new IllegalArgumentException("constructor args cannot be null or empty.");
        }
        this.parentScope = parentScope;
        this.componentResourcePath = componentResourcePath;
    }

    public String getComponentPlatform() {
        return (String) this.parentScope.getNode()
            .tryGetContext("everythingbiig-aws-imagepipelines/etherythingbiig:componentPlatform");
    }

    public String getComponentVersion() {
        return (String) this.parentScope.getNode()
            .tryGetContext("everythingbiig-aws-imagepipelines/etherythingbiig:componentVersion");
    }

    public List<CfnComponent> getComponents(Construct scope) {
        if (this.components == null) {
            try {
                this.components = new ArrayList<CfnComponent>();
                File[] componentResourcePathFiles = Paths.get(ComponentHelper.class.getResource(this.componentResourcePath).toURI()).toFile().listFiles();
                if (componentResourcePathFiles != null) {
                    for (int i = 0; i < componentResourcePathFiles.length; i++) {
                        File componentResourcePathFile = componentResourcePathFiles[i];

                        if (!isYamlFile(componentResourcePathFile)) {
                            continue;
                        }
                        CfnComponent newComponent = CfnComponent.Builder.create(scope, getLogicalName(componentResourcePathFile))
                            .name(getName(componentResourcePathFile))
                            .description(getDescription(componentResourcePathFile))
                            .platform(getComponentPlatform())
                            .data(getData(componentResourcePathFile))
                            .version(getComponentVersion())
                            .build();
                        components.add(newComponent);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                //TODO Fix error handling
            }
        }
        return components;
    }

    public List<CfnImageRecipe.ComponentConfigurationProperty> getComponentConfigurationProperties(Construct scope) {
        List<CfnImageRecipe.ComponentConfigurationProperty> componentConfigs = new ArrayList<CfnImageRecipe.ComponentConfigurationProperty>();
        List<CfnComponent> components = getComponents(scope);
        for (CfnComponent component : components) {
            CfnImageRecipe.ComponentConfigurationProperty compConfigProp = CfnImageRecipe.ComponentConfigurationProperty
                .builder()
                .componentArn(component.getAttrArn())
                .build();
            componentConfigs.add(compConfigProp);
        }
        return componentConfigs;
    }

    private boolean isYamlFile(File file) {
        return file != null && file.isFile() 
            && (file.getName().endsWith(".yaml") || file.getName().endsWith(".yml"));
    }

    private String getData(File componentYaml) throws IOException {
        return substituteParameters(new String(
            Files.readAllBytes(
                Paths.get(
                    componentYaml.toURI())),
                    StandardCharsets.UTF_8
        ));
    }

    private String substituteParameters(String unsubstituted) {
        String substituted = new String(unsubstituted);
        for (String parameter : getParamters().keySet()) {
            substituted = substituted.replace(String.format("${{%s}}", parameter), getParamters().get(parameter));
        }
        return substituted;
    }

    public Map<String, String> getParamters() {
        return this.parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    private String getDescription(File componentYaml) {
        return String.format("%s component.", getFileNameWithoutExtension(componentYaml));
    }

    protected String getName(File component) {
        return getFileNameWithoutExtension(component);
    }

    protected String getFileNameWithoutExtension(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }

        return file.getName().substring(0, file.getName().indexOf("."));
    }

    private String getLogicalName(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }

        String dashedName = getFileNameWithoutExtension(file);

        return CaseUtils.toCamelCase(dashedName, false, '-');
    }
}

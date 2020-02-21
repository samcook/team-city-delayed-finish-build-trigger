/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.guskovartem.teamcityDelayedFinishPlugin;

import static com.github.guskovartem.teamcityDelayedFinishPlugin.DelayedBuildFinishTriggerConstants.EDIT_URL_BUILD_TRIGGER_HTML;
import static com.github.guskovartem.teamcityDelayedFinishPlugin.DelayedBuildFinishTriggerConstants.TRIGGER_CONFIGURATION_PROPERTY;
import static com.github.guskovartem.teamcityDelayedFinishPlugin.DelayedBuildFinishTriggerConstants.WAIT_TIME_PROPERTY;

import java.util.ArrayList;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerService;
import jetbrains.buildServer.buildTriggers.BuildTriggeringPolicy;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DelayedBuildFinishedTrigger extends BuildTriggerService {

  private PluginDescriptor descriptor;
  private ProjectManager projectManager;

  public DelayedBuildFinishedTrigger(@NotNull PluginDescriptor descriptor, @NotNull ProjectManager projectManager) {
    super();
    this.descriptor = descriptor;
    this.projectManager = projectManager;
  }

  @NotNull
  @Override
  public String getName() {
    return "DelayedFinishedBuildTrigger";
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Delayed Finished Build Trigger";
  }

  @NotNull
  @Override
  public String describeTrigger(@NotNull BuildTriggerDescriptor trigger) {
    Integer waitTime = DelayedBuildFinishedUtils.getWaitTime(trigger.getProperties());

    String minutesString = "minutes";

    if (waitTime == 1) {
      minutesString = "minute";
    }

    String triggerDescription = "Wait " + waitTime + " " + minutesString + " after a ";

    if (DelayedBuildFinishedUtils.getSuccessfulBuildsOnlyConfiguration(trigger.getProperties())) {
      triggerDescription += "successful ";
    }

    String buildId = DelayedBuildFinishedUtils.getDependentBuildConfigurationId(trigger.getProperties());

    triggerDescription += "build in " + DelayedBuildFinishedUtils.getDependentBuild(buildId, projectManager).getFullName();

    return triggerDescription;
  }

  @NotNull
  @Override
  public BuildTriggeringPolicy getBuildTriggeringPolicy() {
    return new DelayedBuildFinishedTriggeringPolicy(projectManager);
  }

  @Nullable
  @Override
  public String getEditParametersUrl() {
    return descriptor.getPluginResourcesPath(EDIT_URL_BUILD_TRIGGER_HTML);
  }

  @Override
  public PropertiesProcessor getTriggerPropertiesProcessor() {
    return properties -> {
      final ArrayList<InvalidProperty> invalidProps = new ArrayList<InvalidProperty>();
      final String triggerBuildId = properties.get(TRIGGER_CONFIGURATION_PROPERTY);
      if (StringUtil.isEmptyOrSpaces(triggerBuildId)) {
        invalidProps.add(new InvalidProperty(TRIGGER_CONFIGURATION_PROPERTY, "Trigger Build must be specified"));
      }

      final SBuildType build = DelayedBuildFinishedUtils.getDependentBuild(properties.get(TRIGGER_CONFIGURATION_PROPERTY), projectManager);
      if (build == null) {
        invalidProps.add(new InvalidProperty(TRIGGER_CONFIGURATION_PROPERTY, "Trigger build must exist"));
      }

      final String waitTime = properties.get(WAIT_TIME_PROPERTY);
      if (StringUtil.isEmptyOrSpaces(triggerBuildId)) {
        invalidProps.add(new InvalidProperty(WAIT_TIME_PROPERTY, "Wait Time must be specified"));
      }

      if (!StringUtil.isNumber(properties.get(WAIT_TIME_PROPERTY))) {
        invalidProps.add(new InvalidProperty(WAIT_TIME_PROPERTY, "Wait Time must be a number"));
      }

      return invalidProps;
    };
  }

  @Override
  public boolean isMultipleTriggersPerBuildTypeAllowed() {
    return true;
  }
}

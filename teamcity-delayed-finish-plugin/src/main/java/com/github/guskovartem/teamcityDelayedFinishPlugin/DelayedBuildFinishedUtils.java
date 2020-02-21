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

import static com.github.guskovartem.teamcityDelayedFinishPlugin.DelayedBuildFinishTriggerConstants.AFTER_SUCCESSFUL_BUILD_ONLY_PROPERTY;
import static com.github.guskovartem.teamcityDelayedFinishPlugin.DelayedBuildFinishTriggerConstants.TRIGGER_CONFIGURATION_PROPERTY;
import static com.github.guskovartem.teamcityDelayedFinishPlugin.DelayedBuildFinishTriggerConstants.WAIT_TIME_PROPERTY;

import java.util.Map;
import java.util.Optional;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class DelayedBuildFinishedUtils {
  public Integer getWaitTime(@NotNull Map<String, String> properties) {
    return Integer.parseInt(properties.get(WAIT_TIME_PROPERTY));
  }

  public Boolean getSuccessfulBuildsOnlyConfiguration(@NotNull Map<String, String> properties) {
    return Boolean.parseBoolean(properties.get(AFTER_SUCCESSFUL_BUILD_ONLY_PROPERTY));
  }

  public String getDependentBuildConfigurationId(@NotNull Map<String, String> properties) {
    return properties.get(TRIGGER_CONFIGURATION_PROPERTY);
  }

  public SBuildType getDependentBuild(String triggerBuildId, ProjectManager projectManager) {
    return Optional.ofNullable(projectManager.findBuildTypeById(triggerBuildId))
        .orElseGet(() -> projectManager.findBuildTypeByExternalId(triggerBuildId));
  }
}

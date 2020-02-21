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

import static com.github.guskovartem.teamcityDelayedFinishPlugin.DelayedBuildFinishTriggerConstants.LAST_BUILD_ID_KEY;

import com.intellij.openapi.diagnostic.Logger;
import java.util.Date;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.buildTriggers.PolledBuildTrigger;
import jetbrains.buildServer.buildTriggers.PolledTriggerContext;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.SFinishedBuild;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class DelayedBuildFinishedTriggeringPolicy extends PolledBuildTrigger {

  private static final Logger LOG = Loggers.SERVER;

  private final ProjectManager projectManager;

  @Override
  public void triggerActivated(@NotNull PolledTriggerContext context) throws BuildTriggerException {
    saveLastFinishedBuild(context);
  }

  @Override
  public void triggerBuild(@NotNull PolledTriggerContext context) throws BuildTriggerException {
    String lastTriggeredId = getLastDependentBuildIdTrigeredFor(context);

    SFinishedBuild lastTriggerBuild = getLastFinishedBuild(context);

    if (lastTriggerBuild != null) {
      if (!lastTriggeredId.equalsIgnoreCase(Long.toString(lastTriggerBuild.getBuildId()))) {
        int waitTime = getWaitTime(context);

        Date triggerTime = new Date(lastTriggerBuild.getFinishDate().getTime() + waitTime * 60 * 1000L);

        if (triggerTime.before(new Date())) {
          String minutesString = "minutes";

          if (waitTime == 1) {
            minutesString = "minute";
          }

          context
              .getBuildType()
              .addToQueue(
                  lastTriggerBuild.getFullName()
                      + ", #"
                      + lastTriggerBuild.getBuildNumber()
                      + ", delayed by "
                      + getWaitTime(context)
                      + " "
                      + minutesString);
          context.getCustomDataStorage().putValue(LAST_BUILD_ID_KEY, Long.toString(lastTriggerBuild.getBuildId()));
        }
      }
    }
  }

  private String getDependentBuildConfigurationId(@NotNull PolledTriggerContext context) {
    return DelayedBuildFinishedUtils.getDependentBuildConfigurationId(context.getTriggerDescriptor().getProperties());
  }

  private Boolean getSuccessfulBuildsOnlyConfiguration(@NotNull PolledTriggerContext context) {
    return DelayedBuildFinishedUtils.getSuccessfulBuildsOnlyConfiguration(context.getTriggerDescriptor().getProperties());
  }

  private Integer getWaitTime(@NotNull PolledTriggerContext context) {
    return DelayedBuildFinishedUtils.getWaitTime(context.getTriggerDescriptor().getProperties());
  }

  private String getLastDependentBuildIdTrigeredFor(@NotNull PolledTriggerContext context) {
    return context.getCustomDataStorage().getValue(LAST_BUILD_ID_KEY);
  }

  private void saveLastFinishedBuild(@NotNull PolledTriggerContext context) {
    SFinishedBuild lastBuild = getLastFinishedBuild(context);

    if (lastBuild != null) {
      context.getCustomDataStorage().putValue(LAST_BUILD_ID_KEY, Long.toString(lastBuild.getBuildId()));
    } else {
      context.getCustomDataStorage().putValue(LAST_BUILD_ID_KEY, "");
    }
  }

  private SFinishedBuild getLastFinishedBuild(@NotNull PolledTriggerContext context) {
    String triggerBuildId = getDependentBuildConfigurationId(context);
    SBuildType triggerBuild = DelayedBuildFinishedUtils.getDependentBuild(triggerBuildId, projectManager);

    if (triggerBuild != null) {
      if (getSuccessfulBuildsOnlyConfiguration(context)) {
        return triggerBuild.getLastChangesSuccessfullyFinished();
      } else {
        return triggerBuild.getLastChangesFinished();
      }
    }

    return null;
  }
}

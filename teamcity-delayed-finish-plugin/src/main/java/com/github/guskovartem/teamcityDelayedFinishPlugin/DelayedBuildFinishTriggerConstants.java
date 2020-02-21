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

public class DelayedBuildFinishTriggerConstants {
  public static final String AFTER_SUCCESSFUL_BUILD_ONLY_PROPERTY = "afterSuccessfulBuildOnly";
  public static final String TRIGGER_CONFIGURATION_PROPERTY = "trigger_configuration";
  public static final String WAIT_TIME_PROPERTY = "wait_time";

  public static final String EDIT_URL_BUILD_TRIGGER = "delayedBuildTriggerConfig";
  public static final String EDIT_URL_BUILD_TRIGGER_JSP = EDIT_URL_BUILD_TRIGGER + ".jsp";
  public static final String EDIT_URL_BUILD_TRIGGER_HTML = EDIT_URL_BUILD_TRIGGER + ".html";

  public static final String LAST_BUILD_ID_KEY = "LastTriggeredBuildId";
}

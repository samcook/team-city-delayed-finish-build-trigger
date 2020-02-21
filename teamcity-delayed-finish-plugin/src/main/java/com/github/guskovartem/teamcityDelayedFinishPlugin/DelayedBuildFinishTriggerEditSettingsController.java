/*
   Based on Example code provided on TeamCity Plugin Forums: http://devnet.jetbrains.com/message/5463043#5463043
*/

package com.github.guskovartem.teamcityDelayedFinishPlugin;

import static com.github.guskovartem.teamcityDelayedFinishPlugin.DelayedBuildFinishTriggerConstants.EDIT_URL_BUILD_TRIGGER_HTML;
import static com.github.guskovartem.teamcityDelayedFinishPlugin.DelayedBuildFinishTriggerConstants.EDIT_URL_BUILD_TRIGGER_JSP;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

public class DelayedBuildFinishTriggerEditSettingsController extends BaseController {
  private final ProjectManager myProjectManager;
  private String myPluginResourcesPath;

  public DelayedBuildFinishTriggerEditSettingsController(
      final ProjectManager projectManager, @NotNull final PluginDescriptor pluginDescriptor, @NotNull final WebControllerManager manager) {
    myProjectManager = projectManager;
    myPluginResourcesPath = pluginDescriptor.getPluginResourcesPath(EDIT_URL_BUILD_TRIGGER_JSP);
    manager.registerController(pluginDescriptor.getPluginResourcesPath(EDIT_URL_BUILD_TRIGGER_HTML), this);
  }

  protected ModelAndView doHandle(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) throws Exception {
    DelayedBuildFinishedSettingsBean data = new DelayedBuildFinishedSettingsBean(myProjectManager);

    ModelAndView mv = new ModelAndView(myPluginResourcesPath);
    mv.getModel().put("delayedFinishedBuildTriggerBean", data);
    return mv;
  }
}

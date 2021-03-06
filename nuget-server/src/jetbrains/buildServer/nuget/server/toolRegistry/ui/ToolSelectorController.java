/*
 * Copyright 2000-2011 JetBrains s.r.o.
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

package jetbrains.buildServer.nuget.server.toolRegistry.ui;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.controllers.BasePropertiesBean;
import jetbrains.buildServer.nuget.server.settings.SettingsSection;
import jetbrains.buildServer.nuget.server.settings.tab.ServerSettingsTab;
import jetbrains.buildServer.nuget.server.toolRegistry.NuGetInstalledTool;
import jetbrains.buildServer.nuget.server.toolRegistry.NuGetToolManager;
import jetbrains.buildServer.nuget.server.toolRegistry.tab.InstalledToolsController;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 16.08.11 10:21
 */
public class ToolSelectorController extends BaseController {
  private final NuGetToolManager myToolManager;
  private final PluginDescriptor myDescriptor;
  private final String myPath;

  public ToolSelectorController(@NotNull final NuGetToolManager toolManager,
                                @NotNull final PluginDescriptor descriptor,
                                @NotNull final WebControllerManager web) {
    myToolManager = toolManager;
    myDescriptor = descriptor;
    myPath = descriptor.getPluginResourcesPath("tool/runnerSettings.html");
    web.registerController(myPath, this);
  }

  @NotNull
  public String getPath() {
    return myPath;
  }

  @Override
  protected ModelAndView doHandle(@NotNull final HttpServletRequest request,
                                  @NotNull final HttpServletResponse response) throws Exception {
    final String name = safe(request.getParameter("name"));
    String value = parseValue(request, "value", name);
    final Collection<ToolInfo> tools = getTools();
    final ToolInfo bundledTool = ensureVersion(value, tools);

    if (!StringUtil.isEmptyOrSpaces(request.getParameter("view"))) {
      ModelAndView mv = new ModelAndView(myDescriptor.getPluginResourcesPath("tool/runnerSettingsView.jsp"));
      if (bundledTool != null) {
        mv.getModel().put("tool", bundledTool.getVersion());
        mv.getModel().put("bundled", true);
      } else {
        mv.getModel().put("tool", value);
        mv.getModel().put("bundled", false);
      }
      return mv;
    } else {
      ModelAndView mv = new ModelAndView(myDescriptor.getPluginResourcesPath("tool/runnerSettingsEdit.jsp"));
      mv.getModel().put("name", name);
      mv.getModel().put("value", value);
      mv.getModel().put("customValue", safe(parseValue(request, "customValue", "nugetCustomPath")));
      mv.getModel().put("clazz", safe(request.getParameter("class")));
      mv.getModel().put("style", safe(request.getParameter("style")));
      mv.getModel().put("items", tools);
      mv.getModel().put("settingsUrl", "/admin/admin.html?init=1&item=" + ServerSettingsTab.TAB_ID + "&" + SettingsSection.SELECTED_SECTION_KEY + "=" + InstalledToolsController.SETTINGS_PAGE_ID) ;
      return mv;
    }
  }

  @Nullable
  private ToolInfo ensureVersion(@NotNull final String version, @NotNull Collection<ToolInfo> actionInfos) {
    if (!version.startsWith("?")) return null;
    for (ToolInfo actionInfo : actionInfos) {
      if (actionInfo.getId().equals(version)) return actionInfo;
    }
    final ToolInfo notInstalled = new ToolInfo(version, "Not Installed: " + version.substring(1));
    actionInfos.add(notInstalled);
    return notInstalled;
  }

  @NotNull
  private Collection<ToolInfo> getTools() {
    final ArrayList<ToolInfo> result = new ArrayList<ToolInfo>();
    for (NuGetInstalledTool nuGetInstalledTool : myToolManager.getInstalledTools()) {
      result.add(new ToolInfo(nuGetInstalledTool));
    }
    return result;
  }

  @NotNull
  private static String safe(@Nullable String s) {
    if (s == null || StringUtil.isEmptyOrSpaces(s)) return "";
    return s;
  }

  @NotNull
  private String parseValue(@NotNull final HttpServletRequest request,
                            @NotNull final String requestName,
                            @NotNull final String propertyName) {
    String value = null;

    final BasePropertiesBean bean = (BasePropertiesBean)request.getAttribute("propertiesBean");
    if (bean != null) {
      value = bean.getProperties().get(propertyName);
    }
    if (value == null) {
      value = request.getParameter(requestName);
    }
    if (value == null) {
      value = "";
    }
    return value;
  }
}

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

package jetbrains.buildServer.nuget.server.publish;

import jetbrains.buildServer.nuget.common.PackagesConstants;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 21.07.11 13:51
 */
public class PublishBean {
  public String getNuGetPathKey() { return PackagesConstants.NUGET_PATH; }
  public String getNuGetSourceKey() { return PackagesConstants.NUGET_PUBLISH_SOURCE; }
  public String getApiKey() { return PackagesConstants.NUGET_API_KEY; }
  public String getNuGetPublishFilesKey() {return PackagesConstants.NUGET_PUBLISH_FILES; }
  public String getNuGetPublishCreateOnlyKey() { return PackagesConstants.NUGET_PUBLISH_CREATE_ONLY; }
}
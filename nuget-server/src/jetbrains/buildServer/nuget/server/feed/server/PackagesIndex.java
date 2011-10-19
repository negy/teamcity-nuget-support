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

package jetbrains.buildServer.nuget.server.feed.server;

import jetbrains.buildServer.serverSide.metadata.ArtifactsMetadataEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 19.10.11 16:17
 */
public interface PackagesIndex {
  public static final String TEAMCITY_BUILD_ID = "teamcity.buildId";
  public static final String TEAMCITY_BUILD_TYPE_ID = "teamcity.buildTypeId";
  public static final String TEAMCITY_BUILD_TYPE_NAME = "teamcity.buildTypeName";
  public static final String TEAMCITY_ARTIFACT_RELPATH = "teamcity.artifactPath";
  public static final String TEAMCITY_DOWNLOAD_URL = "teamcity.downloadUrl";

  @NotNull
  Iterator<ArtifactsMetadataEntry> getEntries();
}
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

package jetbrains.buildServer.nuget.server.trigger;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.buildTriggers.BuildTriggerDescriptor;
import jetbrains.buildServer.buildTriggers.BuildTriggerException;
import jetbrains.buildServer.nuget.server.trigger.impl.*;
import jetbrains.buildServer.serverSide.CustomDataStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 14.07.11 15:41
 */
public class NamedPackagesUpdateChecker implements TriggerUpdateChecker {
  private static final Logger LOG = Logger.getInstance(NamedPackagesUpdateChecker.class.getName());

  public static final String KEY = "hash";

  private final PackageChangesManager myPackageChangesManager;
  private final TriggerRequestFactory myRequestFactory;
  private final PackagesHashCalculator myCalculator;


  public NamedPackagesUpdateChecker(@NotNull final PackageChangesManager packageChangesManager,
                                    @NotNull final TriggerRequestFactory requestFactory,
                                    @NotNull final PackagesHashCalculator calculator) {
    myPackageChangesManager = packageChangesManager;
    myRequestFactory = requestFactory;
    myCalculator = calculator;
  }

  @Nullable
  public BuildStartReason checkChanges(@NotNull BuildTriggerDescriptor descriptor,
                                       @NotNull CustomDataStorage storage) throws BuildTriggerException {
    final PackageCheckRequest checkRequest = myRequestFactory.createRequest(descriptor);

    CheckResult result;
    try {
      result = myPackageChangesManager.checkPackage(checkRequest);
      //no change available
    } catch (Throwable t) {
      LOG.warn("Failed to ckeck changes for package: " + checkRequest.getPackage().getPackageId() + ". " + t.getMessage(), t);
      result = CheckResult.failed(t.getMessage());
    }

    if (result == null) return null;
    final String error = result.getError();
    if (error != null) {
      throw new BuildTriggerException("Failed to check for package versions. " + error);
    }

    final String hash = myCalculator.serializeHashcode(result.getInfos());
    final String oldHash = storage.getValue(KEY);

    LOG.debug("Recieved packages hash: " + hash);
    LOG.debug("          old hash was: " + oldHash);
    if (!hash.equals(oldHash)) {
      storage.putValue(KEY, hash);
      storage.flush();

      if (oldHash != null) {
        return new BuildStartReason("NuGet Package " + checkRequest.getPackage().getPackageId() + " updated");
      }
    }

    return null;
  }
}

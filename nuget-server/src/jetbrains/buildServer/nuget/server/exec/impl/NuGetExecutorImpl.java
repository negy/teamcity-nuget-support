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

package jetbrains.buildServer.nuget.server.exec.impl;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import jetbrains.buildServer.ExecResult;
import jetbrains.buildServer.SimpleCommandLineProcessRunner;
import jetbrains.buildServer.nuget.server.exec.*;
import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 14.07.11 12:48
 */
public class NuGetExecutorImpl implements NuGetExecutor {
  private static final Logger LOG = Logger.getInstance(NuGetExecutorImpl.class.getName());

  private final NuGetTeamCityProvider myNuGetTeamCityProvider;

  public NuGetExecutorImpl(@NotNull final NuGetTeamCityProvider nuGetTeamCityProvider) {
    myNuGetTeamCityProvider = nuGetTeamCityProvider;
  }

  @NotNull
  public <T> T executeNuGet(@NotNull final File nugetExePath,
                            @NotNull final List<String> arguments,
                            @NotNull final NuGetOutputProcessor<T> listener) throws NuGetExecutionException {

    GeneralCommandLine cmd = new GeneralCommandLine();
    cmd.setExePath(myNuGetTeamCityProvider.getNuGetRunnerPath().getPath());
    cmd.addParameter(nugetExePath.getPath());
    cmd.addParameters(arguments);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Starting: " + cmd.getCommandLineString());
    }

    final ExecResult result = SimpleCommandLineProcessRunner.runCommand(cmd, new byte[0]);

    if (LOG.isDebugEnabled()) {
      LOG.debug("Exited with code: " + result.getExitCode());
      if (!StringUtil.isEmptyOrSpaces(result.getStdout())) {
        LOG.debug("Output: " + result.getStdout());
      }
      if (!StringUtil.isEmptyOrSpaces(result.getStderr())) {
        LOG.debug("Error: " + result.getStderr());
      }
    }

    listener.onStdOutput(result.getStdout());
    listener.onStdError(result.getStderr());
    listener.onFinished(result.getExitCode());

    return listener.getResult();
  }


  @NotNull
  public NuGetServerHandle startNuGetServer(int port,
                                            @NotNull String serverUrl,
                                            @NotNull File artifactPaths,
                                            @NotNull File specs) throws NuGetExecutionException {
    final GeneralCommandLine cmd = new GeneralCommandLine();
    final File path = myNuGetTeamCityProvider.getNuGetServerRunnerPath();
    cmd.setExePath(path.getPath());
    cmd.setWorkingDirectory(path.getParentFile());

    cmd.addParameter("/port:" + port);
    cmd.addParameter("/packageDownloadBaseUrl:" + serverUrl);
    cmd.addParameter("/packageFilesBasePath:" + artifactPaths.getAbsolutePath());
    cmd.addParameter("/packageSpecFile:" + specs.getAbsolutePath());


    final Process process;
    try {
      process = cmd.createProcess();
    } catch (ExecutionException e1) {
      throw new NuGetExecutionException("Failed to start NuGet server process from: " + cmd.getCommandLineString());
    }

    try {
      process.getInputStream().close();
    } catch (IOException e) {
      LOG.warn("Failed to close input stream of process. " + e.getMessage(), e);
    }

    final OSProcessHandler hander = new OSProcessHandler(process, cmd.getCommandLineString()) {
      @Override
      public Charset getCharset() {
        return cmd.getCharset();
      }
    };

    final AtomicBoolean isRunning = new AtomicBoolean(true);
    final Logger outputLog = Logger.getInstance(getClass().getName() + ".Server");
    hander.addProcessListener(new ProcessAdapter() {
      @Override
      public void onTextAvailable(ProcessEvent event, Key outputType) {
        outputLog.info(outputType + " - " + event.getText());
      }

      @Override
      public void startNotified(ProcessEvent event) {
        outputLog.info("NuGet server process is started. ");
      }

      @Override
      public void processTerminated(ProcessEvent event) {
        outputLog.info("NuGet server process is terminated. ");
      }
    });

    hander.startNotify();
    return new NuGetServerHandle() {
      public boolean isAlive() {
        return isRunning.get();
      }

      public void stop() {
        hander.destroyProcess();
      }
    };
  }

}

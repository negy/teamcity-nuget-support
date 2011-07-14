<%--
  ~ Copyright 2000-2011 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="ib" class="jetbrains.buildServer.nuget.server.trigger.TriggerBean" scope="request"/>

<tr>
  <td>NuGet path:<l:star/></td>
  <td>
    <props:textProperty name="${ib.nuGetExeKey}" style="width:20em;" />
    <span class="smallNote">Specify path to NuGet.exe </span>
    <span class="error" id="error_${ib.sourceKey}"></span>
  </td>
</tr>

<tr>
  <td>NuGet package source:<l:star/></td>
  <td>
    <props:textProperty name="${ib.sourceKey}" style="width:20em;" />
    <span class="smallNote">Specify NuGet packages repository to monitor packages changes</span>
    <span class="error" id="error_${ib.sourceKey}"></span>
  </td>
</tr>

<tr>
  <td>Package Ids<l:star/>:</td>
  <td>
    <props:textProperty name="${ib.packageKey}" style="width:20em;"/>
    <span class="smallNote">Specify package Id to check for updates.</span>
    <span class="error" id="error_${ib.packageKey}"></span>
  </td>
</tr>

<tr>
  <td>Package Version Spec:</td>
  <td>
    <props:textProperty name="${ib.versionKey}" style="width:20em;"/>
    <span class="smallNote">Specify package version to check. Leave empty to check for latest version</span>
    <span class="error" id="error_${ib.versionKey}"></span>
  </td>
</tr>
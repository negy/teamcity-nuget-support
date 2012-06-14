<%--
  ~ Copyright 2000-2012 JetBrains s.r.o.
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
<%@ include file="/include-internal.jsp" %>
<jsp:useBean id="hasToolErrors" type="java.lang.Boolean" scope="request"/>
<jsp:useBean id="toolErrorsText" type="java.lang.String" scope="request"/>
<html>
<body>
<script type="text/javascript">
  var text = "${util:forJS(toolErrorsText, false , false )}";
  parent.BS.NuGet.Tools.InstallPopup.onFormIFrameReady(text);
</script>
</body>
</html>


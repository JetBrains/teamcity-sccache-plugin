<%--suppress XmlPathReference,XmlUnboundNsPrefix --%>
<%@ page import="com.intellij.util.SmartList" %>
<%@ page import="jetbrains.buildServer.serverSide.BuildTypeSettings" %>
<%@ page import="jetbrains.buildServer.serverSide.SProjectFeatureDescriptor" %>
<%@ page import="jetbrains.buildServer.serverSide.oauth.OAuthConstants" %>
<%@ page import="org.jetbrains.teamcity.sccache.SCCacheConstants" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/include.jsp" %>
<%--
  ~ Copyright 2000-2020 JetBrains s.r.o.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ https://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<jsp:useBean id="keys" class="org.jetbrains.teamcity.sccache.SCCacheBuildFeatureJspKeys"/>

<%--<jsp:useBean id="project" type="jetbrains.buildServer.serverSide.SProject" scope="request"/>--%>
<jsp:useBean id="buildForm" type="jetbrains.buildServer.controllers.admin.projects.EditableBuildTypeSettingsForm"
             scope="request"/>
<jsp:useBean id="presentationProvider" class="org.jetbrains.teamcity.sccache.SCCachePresentationProvider"/>

<%
    BuildTypeSettings configuration = buildForm.getSettings();
    List<SProjectFeatureDescriptor> connections = new SmartList<>();
    for (SProjectFeatureDescriptor descriptor : configuration.getProject().getAvailableFeaturesOfType(OAuthConstants.FEATURE_TYPE)) {
        if (SCCacheConstants.ProjectFeatureSettings.FEATURE_TYPE.equals(descriptor.getParameters().get(OAuthConstants.OAUTH_TYPE_PARAM))) {
            connections.add(descriptor);
        }
    }
    request.setAttribute("connections", connections);
%>

<tr>
    <td><label for="${keys.BACKEND}">Backend connection:</label></td>
    <td>
        <props:selectProperty name="${keys.BACKEND}" className="longField" enableFilter="true">
            <props:option id="backend-unspecified" value="unspecified"
                          selected="${true}">-- Choose connection --</props:option>
            <c:forEach items="${connections}" var="connection">
                <props:option id="${connection.id}"
                              value="${connection.id}">${presentationProvider.getDescription(connection)}</props:option>
            </c:forEach>
        </props:selectProperty>
        <span class="error" id="error_${keys.BACKEND}"></span>
        <span class="smallNote"></span>
        <props:hiddenProperty name="${keys.REQUIREMENT}" value="${keys.REQUIREMENT_VALUE}"/>
    </td>
</tr>
<tr class="advancedSetting">
    <td><label for="${keys.PORT}">Server port:</label></td>
    <td>
        <props:textProperty name="${keys.PORT}"
                            className="longField"/>
        <span class="error" id="error_${keys.PORT}"></span>
        <span class="smallNote"><code>-1</code> means default one (4226) or any free port</span>
    </td>
</tr>

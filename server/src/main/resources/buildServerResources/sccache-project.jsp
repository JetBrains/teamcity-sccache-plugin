<%--suppress XmlPathReference --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/include-internal.jsp" %>
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
<jsp:useBean id="keys" class="org.jetbrains.teamcity.sccache.SCCacheProjectFeatureJspKeys"/>

<jsp:useBean id="project" type="jetbrains.buildServer.serverSide.SProject" scope="request"/>

<style type="text/css">
    .backend-container {
        display: none;
    }
    table.runnerFormTable {
        width: 80%;
    }
    table.runnerFormTable th {
        width: 20em;
    }
</style>

<tr>
    <td><label for="displayName">Display name:</label><l:star/></td>
    <td>
        <props:textProperty name="displayName" className="longField"/>
        <span class="smallNote">Provide some name to distinguish this connection from others.</span>
        <span class="error" id="error_displayName"></span>
    </td>
</tr>
<tr>
    <td><label for="${keys.TYPE}">Backend type:</label></td>
    <td>
        <props:selectProperty name="${keys.TYPE}" onchange="BS.SCCache.onFormChange()" className="longField" enableFilter="true">
            <props:option id="unspecified" value="unspecified">-- Choose type --</props:option>
            <props:option id="${keys.s3_TYPE}" value="${keys.s3_TYPE}">S3</props:option>
        </props:selectProperty>
        <span class="error" id="error_${keys.TYPE}"></span>
        <span class="smallNote"></span>
    </td>
</tr>

<tr class="backend-container backend-s3">
    <td><label for="${keys.s3_BUCKET}">Bucket:</label></td>
    <td>
        <props:textProperty name="${keys.s3_BUCKET}" className="longField textProperty_max-width js_max-width"/>
        <span class="error" id="error_${keys.s3_BUCKET}"></span>
        <span class="smallNote"></span>
    </td>
</tr>
<tr class="backend-container backend-s3">
    <td><label for="${keys.s3_ACCESS_KEY}">Access key:</label></td>
    <td>
        <props:textProperty name="${keys.s3_ACCESS_KEY}" className="longField textProperty_max-width js_max-width"/>
        <span class="error" id="error_${keys.s3_ACCESS_KEY}"></span>
        <span class="smallNote"></span>
    </td>
</tr>
<tr class="backend-container backend-s3">
    <td><label for="${keys.s3_SECRET_KEY}">Secret key:</label></td>
    <td>
        <props:passwordProperty name="${keys.s3_SECRET_KEY}" className="longField passwordProperty_max-width js_max-width"/>
        <span class="error" id="error_${keys.s3_SECRET_KEY}"></span>
        <span class="smallNote"></span>
    </td>
</tr>
<tr class="backend-container backend-s3 advancedSetting">
    <td><label for="${keys.s3_IAM_CREDENTIALS_URL}">AWS IAM credentials URL:</label></td>
    <td>
        <props:textProperty name="${keys.s3_IAM_CREDENTIALS_URL}"
                            className="longField textProperty_max-width js_max-width"/>
        <span class="error" id="error_${keys.s3_IAM_CREDENTIALS_URL}"></span>
        <span class="smallNote">E.g. <code>http://169.254.169.254/latest/meta-data/iam/security-credentials/role_name</code></span>
    </td>
</tr>
<tr class="backend-container backend-s3 advancedSetting">
    <td><label for="${keys.s3_ENDPOINT}">Endpoint:</label></td>
    <td>
        <props:textProperty name="${keys.s3_ENDPOINT}" className="longField textProperty_max-width js_max-width"/>
        <span class="error" id="error_${keys.s3_ENDPOINT}"></span>
        <span class="smallNote">E.g. for minio use '&lt;ip&gt;:&lt;port&gt;'</span>
    </td>
</tr>
<tr class="backend-container backend-s3 advancedSetting">
    <td></td>
    <td>
        <props:checkboxProperty name="${keys.s3_SSL}"/>
        <label for="${keys.s3_SSL}">Use SSL connection</label>
        <span class="error" id="error_${keys.s3_SSL}"></span>
        <span class="smallNote"></span>
    </td>
</tr>

<%--<tr class="backend-container backend-s3">--%>
<%--    <td><label for="${keys.URL}">:</label></td>--%>
<%--    <td>--%>
<%--        <props:textProperty name="${keys.URL}" className="longField textProperty_max-width js_max-width"/>--%>
<%--        <span class="error" id="error_${keys.URL}"/>--%>
<%--        <span class="smallNote"></span>--%>
<%--    </td>--%>
<%--</tr>--%>

<script type="text/javascript">
    BS.SCCache = {
        onFormChange: function () {
            BS.Util.hide($j('.backend-container'));

            let type = $j('#${keys.TYPE}').val();

            switch (type) {
                case '${keys.s3_TYPE}':
                    BS.Util.show($j('.backend-${keys.s3_TYPE}'));
                    break;
                case 'unspecified':
                    break;
                default :
                    throw "Invalid state exception, cannot process type: " + type;
            }
            BS.VisibilityHandlers.updateVisibility('mainContent');
        },
    };
    BS.SCCache.onFormChange();
</script>



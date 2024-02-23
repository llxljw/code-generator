package ${basePackage}.model;

import lombok.Data;

/**
* 数据模型
*/
<#macro generateModel indent modelInfo>
    <#if modelInfo.description??>
        ${indent}/**
        ${indent} * ${modelInfo.description}
        ${indent} */
    </#if>
    ${indent}public ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#macro>
@Data
public class DataModel {
<#list modelConfig.modelInfo as modelInfo>

    <#if modelInfo.groupKey??>
        public ${modelInfo.type}  ${modelInfo.groupKey} = new ${modelInfo.type}();
        @Data
        public static class ${modelInfo.type} {
        <#list modelInfo.models as modelInfo>
            <@generateModel "        " modelInfo/>
        </#list>
        }
    <#else>
        <@generateModel "    " modelInfo/>
    </#if>
</#list>
}

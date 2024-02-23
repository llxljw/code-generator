package ${basePackage}.generator;
import freemarker.template.TemplateException;
import com.ljw.model.DataModel;
import java.io.File;
import java.io.IOException;

<#macro generateFile indent fileInfo>
    ${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
    ${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
    <#if fileInfo.generateType == "static">
        ${indent}StaticGenerator.copyFilesByHutool(inputPath, outputPath);
    <#else>
        ${indent}DynamicGenerator.doGenerate(inputPath, outputPath, model);
    </#if>
</#macro>
/**
 * @Description: 文件生成
 */
public class MainGenerator {
    public static void doGenerate(DataModel model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
<#list modelConfig.modelInfo as modelInfo>
<#-- 有分组 -->
    <#if modelInfo.groupKey??>
        <#list modelInfo.models as subModelInfo>
            ${subModelInfo.type} ${subModelInfo.fieldName} = model.${modelInfo.groupKey}.${subModelInfo.fieldName};
        </#list>
    <#else>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
    </#if>
</#list>
<#list fileConfig.fileInfo as fileInfo>
    <#if fileInfo.groupKey??>
        <#if fileInfo.condition??>
            if (${fileInfo.condition}) {
            <#list fileInfo.files as fileInfo>
                <@generateFile indent="            " fileInfo=fileInfo />
            </#list>
            }
        <#else>

            <@generateFile indent="        " fileInfo=fileInfo />
        </#if>
    <#else>
        <#if fileInfo.condition??>
            if (${fileInfo.condition}) {

            <@generateFile indent="            " fileInfo=fileInfo />
            }
        <#else>

            <@generateFile indent="        " fileInfo=fileInfo />
        </#if>
    </#if>
</#list>
    }
}

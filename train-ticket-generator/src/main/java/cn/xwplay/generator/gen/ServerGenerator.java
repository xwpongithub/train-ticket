package cn.xwplay.generator.gen;

import cn.xwplay.generator.util.Field;
import cn.xwplay.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ServerGenerator {
    private static final String ROOT_PATH = "train-ticket-generator/";
    private static final boolean READ_ONLY = false;
    private static final String VUE_PATH = "admin/src/views/main/";
    private static String SERVICE_PATH = "[module]/src/main/cn/xwplay/[module]/";
    /**
     * 当前模块的POM.xml文件所在路径
     */
    private static final String POM_PATH = ROOT_PATH+"pom.xml";

    private static String module;

    public static void main(String[] args) throws Exception {
        // 获取pom.xml中标签为configurationFile里的内容
        var generatorPath = getGeneratorPath();
        System.out.println("POM中配置的模块对应的配置文件所在路径:"+generatorPath);

        // 比如generator-config-member.xml，得到module = member
        module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("生成模块名: " + module);
        SERVICE_PATH = SERVICE_PATH.replace("[module]", module);
        SERVICE_PATH = SERVICE_PATH.replace("/"+module,"/"+module.split("-")[2]);
        System.out.println("生成模块对应的模块目录路径:"+SERVICE_PATH);
//        new File(SERVICE_PATH).mkdirs();
//        System.out.println("servicePath: " + serverPath);
//

        // 读取table节点,取到对应表和domain实体类
//        var document = new SAXReader().read(ROOT_PATH + generatorPath);
//        var table = document.selectSingleNode("//table");
//        var tableName = table.selectSingleNode("@tableName");
//        var domainObjectName = table.selectSingleNode("@domainObjectName");
//        System.out.println(tableName.getText() + "/" + domainObjectName.getText());

//        // 为DbUtil设置数据源
//        Node connectionURL = document.selectSingleNode("//@connectionURL");
//        Node userId = document.selectSingleNode("//@userId");
//        Node password = document.selectSingleNode("//@password");
//        System.out.println("url: " + connectionURL.getText());
//        System.out.println("user: " + userId.getText());
//        System.out.println("password: " + password.getText());
//        DbUtil.url = connectionURL.getText();
//        DbUtil.user = userId.getText();
//        DbUtil.password = password.getText();
//
//        // 示例：表名 jiawa_test
//        // Domain = JiawaTest
//        String Domain = domainObjectName.getText();
//        // domain = jiawaTest
//        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
//        // do_main = jiawa-test
//        String do_main = tableName.getText().replaceAll("_", "-");
//        // 表中文名
//        String tableNameCn = DbUtil.getTableComment(tableName.getText());
//        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
//        Set<String> typeSet = getJavaTypes(fieldList);
//
//        // 组装参数
//        Map<String, Object> param = new HashMap<>();
//        param.put("module", module);
//        param.put("Domain", Domain);
//        param.put("domain", domain);
//        param.put("do_main", do_main);
//        param.put("tableNameCn", tableNameCn);
//        param.put("fieldList", fieldList);
//        param.put("typeSet", typeSet);
//        param.put("readOnly", readOnly);
//        System.out.println("组装参数：" + param);
//
//        gen(Domain, param, "service", "service");
//        gen(Domain, param, "controller/admin", "adminController");
//        gen(Domain, param, "req", "saveReq");
//        gen(Domain, param, "req", "queryReq");
//        gen(Domain, param, "resp", "queryResp");
//
//        genVue(do_main, param);
    }

    private static void gen(String Domain, Map<String, Object> param, String packageName, String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target + ".ftl");
        String toPath = SERVICE_PATH + packageName + "/";
        new File(toPath).mkdirs();
        String Target = target.substring(0, 1).toUpperCase() + target.substring(1);
        String fileName = toPath + Domain + Target + ".java";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }

    private static void genVue(String do_main, Map<String, Object> param) throws IOException, TemplateException {
        FreemarkerUtil.initConfig("vue.ftl");
        new File(VUE_PATH + module).mkdirs();
        String fileName = VUE_PATH + module + "/" + do_main + ".vue";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }

    /**
     * 获取模块对应xml配置文件路径
     */
    private static String getGeneratorPath() throws DocumentException {
        var saxReader = new SAXReader();
        var map = new HashMap<String, String>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        var document = saxReader.read(POM_PATH);
        // 获取pom.xml中标签为configurationFile里的内容
        var node = document.selectSingleNode("//pom:configurationFile");
        return node.getText();
    }

    /**
     * 获取所有的Java类型，使用Set去重
     */
    private static Set<String> getJavaTypes(List<Field> fieldList) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            set.add(field.getJavaType());
        }
        return set;
    }
}

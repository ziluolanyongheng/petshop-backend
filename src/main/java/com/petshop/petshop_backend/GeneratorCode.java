package com.petshop.petshop_backend;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.dialect.IDialect;
import com.zaxxer.hikari.HikariDataSource;

public class GeneratorCode {

    //关闭Main函数，提高部署时间，需要时再开启吧
    public static void main(String[] args) {
        //配置数据源（通过环境变量注入，避免明文提交数据库密码）
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(System.getenv().getOrDefault("GEN_DB_URL", "jdbc:mysql://localhost:3306/pet_shop"));
        dataSource.setUsername(System.getenv().getOrDefault("GEN_DB_USERNAME", "root"));
        dataSource.setPassword(System.getenv().getOrDefault("GEN_DB_PASSWORD", ""));

        // 生成配置
        GlobalConfig globalConfig = createGlobalConfig();
        //通过 datasource 和 globalConfig 创建代码生成器
        //注意第三个参数，设置为MYSQL方言
        Generator generator = new Generator(
                dataSource, globalConfig, IDialect.MYSQL );
        //生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfig() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();
        //设置根包
        globalConfig.getPackageConfig()
                .setBasePackage("com.petshop.petshop_backend");
        //设置表前缀和只生成哪些表，setGenerateTable 未配置时，生成所有表
        globalConfig.getStrategyConfig()
                .setGenerateTable("pet_video");
        //设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true);
        //设置生成 mapper，service和实现类，以及controller
        globalConfig.enableMapper();
        globalConfig.enableService();
        globalConfig.enableServiceImpl();
        globalConfig.enableController();

        return globalConfig;
    }
}

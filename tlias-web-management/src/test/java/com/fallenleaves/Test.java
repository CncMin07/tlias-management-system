package com.fallenleaves;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.Bucket;

import java.util.List;

public class Test {

    public static void main(String[] args) {

        // ✅ 1. 凭证从环境变量读取，不要把 AK / SK 写死在代码里（仓库是公开的）
        String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");

        // ✅ 2. 正确 Region 和 Endpoint
        String region = "cn-shanghai";
        String endpoint = "https://oss-cn-shanghai.aliyuncs.com";

        // ✅ 3. 凭证
        DefaultCredentialProvider provider =
                new DefaultCredentialProvider(accessKeyId, accessKeySecret);

        // ✅ 4. 客户端配置
        ClientBuilderConfiguration config = new ClientBuilderConfiguration();
        config.setSignatureVersion(SignVersion.V4);

        // ✅ 5. 初始化 OSS Client
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .region(region)
                .credentialsProvider(provider)
                .clientConfiguration(config)
                .build();

        // ✅ 6. 列出 Bucket
        List<Bucket> buckets = ossClient.listBuckets();
        System.out.println("Bucket 列表：");

        if (buckets.isEmpty()) {
            System.out.println("暂无 Bucket");
        } else {
            for (Bucket bucket : buckets) {
                System.out.println(bucket.getName());
            }
        }

        // ✅ 7. 关闭
        ossClient.shutdown();
    }
}
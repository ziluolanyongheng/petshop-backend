package com.petshop.petshop_backend.utils;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MinIOUtil {

    //MinIO服务器ip（通过环境变量注入，避免明文提交）
    static private String endpoint = System.getenv().getOrDefault("MINIO_ENDPOINT", "http://localhost:9000");
    //账号accessKey
    static private String accessKey = System.getenv().getOrDefault("MINIO_ACCESS_KEY", "");
    //密码secretKey
    static private String secretKey = System.getenv().getOrDefault("MINIO_SECRET_KEY", "");
    //传的桶名
    static private String filesBucket = System.getenv().getOrDefault("MINIO_BUCKET", "mediafiles");

    static private MinioClient minioClient;

    static public String upload(MultipartFile filedata) throws IOException {

        minioClient =  MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        //创建临时文件
        File tempFile = File.createTempFile("minio", ".temp");
        filedata.transferTo(tempFile);

        //得到临时文件绝对路径
        String absolutePath = tempFile.getAbsolutePath();


        //得到原始文件名
        String originalFilename = filedata.getOriginalFilename();
        //根据后缀名得到mimetype
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String mimeType = getMimeType(extension);


        //通过时间戳获取一个路径
        String defaultFolderPath = getDefaultFolderPath();

        //获得文件md5
        String fileMd5 = getFileMd5(tempFile);

        //得到objectName
        String objectName = defaultFolderPath+fileMd5+extension;

        boolean b = addMediaFilesToMinIO(absolutePath, mimeType, filesBucket, objectName);

        if(!b){
            return null;
        }else{
            //文件路径
            return endpoint+"/"+filesBucket+"/"+objectName;
        }
    }

    //将文件上传到minio
    static  private boolean addMediaFilesToMinIO(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            //上传文件的参数信息
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket(bucket)//确定桶
                    .filename(localFilePath)//指定本地文件路径
                    .object(objectName) //包含完整路径
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(testbucket);
//            log.debug("上传文件到minio成功：bucket：{}，objectName：{}", bucket, objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
//            log.error("上传文件出错：bucket：{}，objectName：{}，错误信息：{}", bucket, objectName, e.getMessage());
        }
        return false;
    }

    //根据扩展名获取mimeType
    static private String getMimeType(String extension) {
        if(extension==null)
            extension = "";
        //通过扩展名得到媒体资源类型 mimeType
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    //获取文件的md5
    static private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取文件默认存储目录路径 年/月/日，包含最后的'/'
    static private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/")+"/";
        return folder;
    }

}

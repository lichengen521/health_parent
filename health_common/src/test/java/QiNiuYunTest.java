import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.junit.Test;

//使用七牛云将本地文件上传到云服务器
public class QiNiuYunTest {
    //实现将本地文件上传云服务器
    @Test
    public void test1(){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = "x8ITIWf6DMRDYQpQqIh2PXISfmFZZ12u3gcKXAjk";
        String secretKey = "7FFi7YqQFy9qy6yho5EyVOR8hZi-liogvrE-Hdg0";
        String bucket = "health-lce";
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
//        String localFilePath = "/home/qiniu/test.png";//linux格式
        String localFilePath = "D:\\\\java-code\\\\git.png";
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = "git.png";//指定上传的文件名 如果不指定那么就会以原始文件名的哈希值作为文件名
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(localFilePath, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
    }

    //实现删除云服务器的文件
    @Test
    public void test2() {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        //...其他参数参考类注释
        String accessKey = "x8ITIWf6DMRDYQpQqIh2PXISfmFZZ12u3gcKXAjk";
        String secretKey = "7FFi7YqQFy9qy6yho5EyVOR8hZi-liogvrE-Hdg0";
        String bucket = "health-lce";
        String key = "git.png";
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }
}

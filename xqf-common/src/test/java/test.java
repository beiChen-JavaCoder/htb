import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

/**
 * @author Xqf
 * @version 1.0
 */
@Slf4j
public class test {

    @Test
    public void test01(){
        log.info("导出数据库开始:" + LocalDateTime.now().toLocalTime());
        //进入数据库命令
        String cmd1 ="mysql -uroot -pxqf && "+"use aurora &&"+"INSERT INTO t_about VALUES(NULL,NULL,LOCALTIME(),LOCALTIME());";
        //备份命令
//        String cmd = ""+"mysqldump -u root -pxqf aurora > ";
        String filePath = "f:\\aurora.sql";
//        start(cmd1 + filePath);
        log.info("导出数据库结束:");

    }
    @Test
    public void start() {
        try {
            Process process = Runtime.getRuntime().exec("mysql -uroot -pxqf &&"+"use aurora &&"+"INSERT INTO t_about VALUES(NULL,NULL,LOCALTIME(),LOCALTIME());");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "gbk"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("任务执行完毕！");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        }
}
}

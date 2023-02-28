package apple.mint.agent.service;

import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path; 
import java.util.Map;
import java.util.HashMap;
 

@Service
public class FileService {

    public Map<String, String> checkDir(String directory) throws Exception{
        Map<String, String> res = new HashMap<String, String>();
        Path path = Paths.get(directory);
        boolean check = Files.exists(Paths.get(directory));
        if (check) {
            boolean isDirectory = Files.isDirectory(path);
            if (!isDirectory) {
                res.put("confirmCd", "3");
                res.put("confirmMsg", "디렉토리아님");
            } else {
                boolean isReadable = Files.isReadable(path);
                if (!isReadable) {
                    res.put("confirmCd", "4");
                    res.put("confirmMsg", "읽기권한없음");
                } else {
                    res.put("confirmCd", "1");
                    res.put("confirmMsg", "확인완료");
                }
            }
        } else {
            res.put("confirmCd", "2");
            res.put("confirmMsg", "디렉토리없음");
        }
        return res;
    }

}

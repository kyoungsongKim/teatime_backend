package castis.domain.board;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Configuration
@Getter
public class FileUtil {
    @Value("${uploadLocation.path}")
    private String uploadPath;
    String uploadFolder = "/usr/local/imsfileupload/";

    /**
     * 파일 업로드.
     */
    public List<FileVO> saveAllFiles(List<MultipartFile> upfiles) {
        String filePath = "/usr/local/imsfileupload/"; 
        List<FileVO> filelist = new ArrayList<FileVO>();

        for (MultipartFile uploadfile : upfiles ) {
            if (uploadfile.getSize() == 0) {
                continue;
            }
            
            String newName = getNewName();
            
            saveFile(uploadfile, filePath + "/" + newName.substring(0,4) + "/", newName);
            
            FileVO filedo = new FileVO();
            filedo.setFilename(uploadfile.getOriginalFilename());
            filedo.setRealname(newName);
            filedo.setFilesize(uploadfile.getSize());
            filelist.add(filedo);
        }
        return filelist;
    }    
    
    /**
     * 파일 저장 경로 생성.
     */
    public void makeBasePath(String path) {
        File dir = new File(path); 
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public String saveFile(MultipartFile file, String newName) throws IOException {
        if (file == null || file.getName().equals("") || file.getSize() < 1) {
            return null;
        }

        makeBasePath(uploadFolder);
        String uploadFileFullPath = uploadFolder + newName;

        File uploadFile = new File(uploadFileFullPath);
        file.transferTo(uploadFile);

        return uploadFileFullPath;
    }

    /**
     * 실제 파일 저장.
     */
    public String saveFile(MultipartFile file, String basePath, String fileName){
        if (file == null || file.getName().equals("") || file.getSize() < 1) {
            return null;
        }
     
        makeBasePath(basePath);
        String serverFullPath = basePath + fileName;
  
        File file1 = new File(serverFullPath);
        try {
            file.transferTo(file1);
        } catch (IllegalStateException ex) {
            System.out.println("IllegalStateException: " + ex.toString());
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.toString());
        }
        
        return serverFullPath;
    }
    
    public void copyFile(String fileName) {
    	String filePath = "/usr/local/imsfileupload/";
		String imsPath = "/usr/local/tomcat/webapps/ROOT/";
		String originalPath = filePath + fileName.substring(0,4) + "/"+ fileName;
		String targetPath = imsPath + fileName;
		File file1 = new File(originalPath);
        if (!file1.exists()) {
        	System.out.println("originalPath:" + originalPath + " is NOT EXIST!");
            return ;
        }
		try (InputStream fis = new FileInputStream(originalPath); OutputStream fos = new FileOutputStream(targetPath)) {
			int bytesRead = 0;
			while ((bytesRead = fis.read()) != -1) {
				fos.write(bytesRead);
			}
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 날짜로 새로운 파일명 부여.
     */
    public String getNewName() {
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        return ft.format(new Date()) + (int) (Math.random() * 10);
    }
    
    public String getFileExtension(String filename) {
          Integer mid = filename.lastIndexOf(".");
          return filename.substring(mid, filename.length());
    }

    public String getRealPath(String path, String filename) {
        return path + filename.substring(0,4) + "/";
    }
}

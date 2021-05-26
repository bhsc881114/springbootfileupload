package com.neo.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class UploadController {
    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "/tmp/";
    private final static Logger logger = LoggerFactory.getLogger(UploadController.class);


    @GetMapping("/")
    public String index() throws Exception {
        try {
            Thread.sleep(1000);
        } catch(Exception e) {
            throw e;
        }
        return "upload";
    }


    static ByteBuffer bb ;

    @RequestMapping(value = "/testmemory")
    @ResponseBody
    public boolean testmemory(int size,int sleep) {
        try {
            if (sleep>0) {
                TimeUnit.MINUTES.sleep(sleep);
            }
            int memoryBlock = size * 1024 * 1024;
            bb = ByteBuffer.allocateDirect(memoryBlock);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
//        return true;
    }

    static TestObject testObj = new TestObject();
    static {
        testObj.setId(createRandomStr1(1000));
        testObj.setName(createRandomStr1(5000));
        testObj.setDesc(createRandomStr1(8000));

        ArrayList<String> al = new ArrayList();
        al.add(createRandomStr1(8000));
        al.add(createRandomStr1(8000));
        al.add(createRandomStr1(8000));

        Map<String,String> map =new HashMap<String,String>();
        map.put(createRandomStr1(100),createRandomStr1(8000));
        map.put(createRandomStr1(100),createRandomStr1(8000));
        map.put(createRandomStr1(100),createRandomStr1(8000));
    }

    public static String createRandomStr1(int length){
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            stringBuffer.append(str.charAt(number));
        }
        return stringBuffer.toString();
    }


    @RequestMapping(value = "/printLog")
    @ResponseBody
    public boolean printLog(int count) {
        for(int i=0;i<count;i++) {
            logger.info(JSON.toJSONString(testObj));
        }
        return true;
    }

    static boolean isRun = true;

    @RequestMapping(value = "/hello")
    @ResponseBody
    public String hello() {
        if (isRun) {
            new Thread(){
                @Override
                public void run() {
                    while(true) {
                        log.info("run hello");
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                        }
                    }
                }
            }.start();
            isRun = false;
        }

        return "ok";
    }

    @RequestMapping(value = "/test1")
    @ResponseBody
    public boolean caseCount(int count) {
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @RequestMapping(value = "/test2")
    @ResponseBody
    public boolean caseCount1(int count) {
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }
//    @PostMapping("/upload") // //new annotation since 4.3
//    public String singleFileUpload(@RequestParam("file") MultipartFile file,
//                                   RedirectAttributes redirectAttributes) {
//        if (file.isEmpty()) {
//            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
//            return "redirect:uploadStatus";
//        }
//
//        try {
//            // Get the file and save it somewhere
//            byte[] bytes = file.getBytes();
//            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
//            Files.write(path, bytes);
//
//            redirectAttributes.addFlashAttribute("message",
//                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return "redirect:/uploadStatus";
//    }


//    @RequestMapping(value="upload", method= RequestMethod.POST)
//    public void upload(HttpServletRequest request) {
//        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
//        if (!isMultipart) {
//            return;
//        }
//        ServletFileUpload upload = new ServletFileUpload();
//        try {
//            FileItemIterator iter = upload.getItemIterator(request);
//            while (iter.hasNext()) {
//                FileItemStream item = iter.next();
//                String name = item.getFieldName();
//                InputStream stream = item.openStream();
//                if (item.isFormField()) {
//                    System.out.println("字段名称：" + name + " 值：" + Streams.asString(stream) );
//                } else {
//                    OutputStream out = new FileOutputStream("/tmp/incoming.gz");
//                    IOUtils.copy(stream, out);
//                    stream.close();
//                    out.close();
//
//                }
//            }
//        }catch (FileUploadException e){
//            e.printStackTrace();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public void upload(HttpServletRequest request) {
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) {
                return;
            }

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload();

            // Parse the request
            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                if (!item.isFormField()) {
                    String filename = item.getName();
                    // Process the input stream
                    OutputStream out = new FileOutputStream(filename);
                    IOUtils.copy(stream, out);
                    stream.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

}
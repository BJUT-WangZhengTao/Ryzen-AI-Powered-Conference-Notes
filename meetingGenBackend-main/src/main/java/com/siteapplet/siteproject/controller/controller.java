package com.siteapplet.siteproject.controller;

import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.utils.Constants;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;


/**
 * @author: danghongbo
 * ===============================
 * Created with IDEA
 * Date: 2022/11/9
 * Time: 23:07
 * ===============================
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173",maxAge = 3600)
public class controller {
    @PostMapping(value = "/uploadFile")
    public String fileUp(@RequestParam("currentFile") MultipartFile[] files, HttpServletRequest req) {
        System.out.println("接收到的文件有" + files.length + "个");
        for(MultipartFile f : files){
            System.out.println("正在存储"+f.getOriginalFilename()+"文件");
            String path = "C:/Users/Administrator/Desktop/files/";
            String name = "原始文字.docx";
            File floder = new File(path);
            if(!floder.isDirectory()) { floder.mkdirs(); }
            try{
                f.transferTo(new File(floder, name));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "上传成功";
    }

    @PostMapping(value = "/uploadText")
    public String fileUpText(@RequestParam("currentText") String text, HttpServletRequest req) throws IOException {
        System.out.println("接收到的文字: " + text);

        String output_path = "C:/Users/Administrator/Desktop/files/";
        String output_name = "原始文字.docx";
        //写入docx文件存在问题，应该使用XWPFDocument来写
        XWPFDocument doc = new XWPFDocument();

        XWPFParagraph para = doc.createParagraph();
        XWPFRun run = para.createRun();
        para.setAlignment(ParagraphAlignment.LEFT);
        run.setText(text);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        FileOutputStream fileOutputStream = null;
        doc.write(bao);
        try {
            fileOutputStream = new FileOutputStream(output_path + output_name);
            fileOutputStream.write(bao.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传成功";
    }

   @PostMapping(value = "/uploadFile2")
   public String fileup2(@RequestParam("currentFile") MultipartFile[] files, HttpServletRequest req) {
       System.out.println("接收到的文件有"+files.length+"个");
       for(MultipartFile f:files){
           System.out.println("正在存储"+f.getOriginalFilename()+"文件");
           String path = "C:/Users/Administrator/Desktop/formatFiles/";
           String name = "会议摘要型格式.docx";
           File floder=new File(path);
           if(!floder.isDirectory()) {
               floder.mkdirs();
           }
           try{
               f.transferTo(new File(floder, name));
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
       return "上传成功";
   }
    @PostMapping(value = "/uploadKeywords")
    public String fileUpKeywords(@RequestParam("currentFile") MultipartFile[] files, HttpServletRequest req) {
        for(MultipartFile f:files){
            System.out.println("正在存储"+f.getOriginalFilename()+"文件");
            String path = "C:/Users/Administrator/Desktop/formatFiles/";
            String name = "关键词.docx";
            File floder=new File(path);
            if(!floder.isDirectory()) {
                floder.mkdirs();
            }
            try{
                f.transferTo(new File(floder, name));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "上传成功";
    }

    public List<String> callWithMessage(List<String> origin_text)
            throws NoApiKeyException, ApiException, InputRequiredException {
        Generation gen = new Generation();
        Constants.apiKey="sk-df302754ac424760b1961b64dab5020d";

        String prompt_path = "C:/Users/Administrator/Desktop/formatFiles/";
        String prompt_name = "会议摘要型格式.docx";
        String send_text = String.join(" ", origin_text);
        String prompt_text = String.join(" ", readTxtFile(prompt_path, prompt_name));
        System.out.println(prompt_text);
        MessageManager msgManager = new MessageManager(10);
        Message systemMsg =
                Message.builder().role(Role.SYSTEM.getValue()).content(prompt_text).build();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(send_text).build();
        msgManager.add(systemMsg);
        msgManager.add(userMsg);
        QwenParam param =
                QwenParam.builder().model(Generation.Models.QWEN_MAX).messages(msgManager.get())
                        .resultFormat(QwenParam.ResultFormat.MESSAGE)
                        .topP(0.8)
                        .enableSearch(true)
                        .build();

        GenerationResult result = gen.call(param);
        msgManager.add(result);
        String back_text = result.getOutput().getChoices().get(0).getMessage().getContent();
        List<String> return_text = new ArrayList<String>(Arrays.asList(back_text.split("\n")));
        return return_text;
    }

    public static List<String> readTxtFile(String filepath, String fileName) {
        List<String> lines = new ArrayList<>();
        try {
            XWPFDocument doc = new XWPFDocument(new FileInputStream(filepath + fileName));
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                String text = paragraph.getText();
                // 对文本进行解码
                String decodedText = new String(text.getBytes("UTF-8"), "UTF-8");
                lines.add(decodedText);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return lines;
    }

    @RequestMapping(value = "/downloadMeeting")
    public void downloadMeeting(HttpServletResponse response) throws IOException, NoApiKeyException, InputRequiredException {
        String meeting_path = "C:/Users/Administrator/Desktop/files/";
        String meeting_name = "原始文字.docx";
        List<String> origin_text = readTxtFile(meeting_path, meeting_name);

        List<String> chinese_text = callWithMessage(origin_text);

        String output_path = "C:/Users/Administrator/Desktop/files/";
        String output_name = "会议纪要.docx";

        System.out.println("中文纪要大小：" + chinese_text.size());

        boolean translate_englist = false;
        boolean translate_portuguese = false;

        String languages_path = "C:/Users/Administrator/Desktop/files/";
        String languages_name = "语言.docx";

        List<String> languages = readTxtFile(languages_path, languages_name);

        for (String language : languages) {
            if (language.equals("英语")) {
                translate_englist = true;
            }
            if (language.equals("葡语")) {
                translate_portuguese = true;
            }
        }

        List<String> english_text = new ArrayList<>();
        List<String> portuguese_text = new ArrayList<>();

        if (translate_englist) {
            english_text = translate(chinese_text, "请遵从一样的格式帮我翻译成英文，注意换行");
            System.out.println("英文纪要大小：" + english_text.size());
        }
        if (translate_portuguese) {
            portuguese_text = translate(chinese_text, "请遵从一样的格式帮我翻译成葡语，注意换行");
            System.out.println("葡语纪要大小：" + portuguese_text.size());
        }

        List<String> new_text = new ArrayList<>();
        for (int i = 0; i < chinese_text.size(); i++) {
            new_text.add(chinese_text.get(i));
            if (chinese_text.get(i) != "") {
                if (translate_englist) {
                    new_text.add(english_text.get(i));
                }
                if (translate_portuguese) {
                    new_text.add(portuguese_text.get(i));
                }
            }
        }

        //写入docx文件存在问题，应该使用XWPFDocument来写
        XWPFDocument doc = new XWPFDocument();

        for (String s : new_text) {
            XWPFParagraph para = doc.createParagraph();
            XWPFRun run = para.createRun();
            para.setAlignment(ParagraphAlignment.LEFT);
            run.setText(s);
        }
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        FileOutputStream fileOutputStream = null;
        doc.write(bao);
        try {
            fileOutputStream = new FileOutputStream(output_path + output_name);
            fileOutputStream.write(bao.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.setHeader("Content-Disposition", "attachment;filename=" + output_name);
        response.setHeader("resource-filename", output_name);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // 写入响应流
        ServletOutputStream outputStream = response.getOutputStream();
        for (String line : new_text) {
            outputStream.write(line.getBytes());
            outputStream.write('\n');
            outputStream.flush();
        }
    }

    @RequestMapping(value = "/paramSettingUpload")
    public void paramSetting(@RequestParam("checkedLanguages") String [] languages) throws IOException {
        String output_path = "C:/Users/Administrator/Desktop/formatFiles/";
        String output_name = "语言.docx";
        //写入docx文件存在问题，应该使用XWPFDocument来写
        XWPFDocument doc = new XWPFDocument();

        for (String s : languages) {
            XWPFParagraph para = doc.createParagraph();
            XWPFRun run = para.createRun();
            para.setAlignment(ParagraphAlignment.LEFT);
            run.setText(s);
        }
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        FileOutputStream fileOutputStream = null;
        doc.write(bao);
        try {
            fileOutputStream = new FileOutputStream(output_path + output_name);
            fileOutputStream.write(bao.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> translate(List<String> origin_text, String language) throws NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Constants.apiKey="sk-df302754ac424760b1961b64dab5020d";

        String send_text = String.join("\n", origin_text);
        String prompt_text = String.join(" ", language);

        MessageManager msgManager = new MessageManager(10);
        Message systemMsg =
                Message.builder().role(Role.SYSTEM.getValue()).content(prompt_text).build();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(send_text).build();
        msgManager.add(systemMsg);
        msgManager.add(userMsg);
        QwenParam param =
                QwenParam.builder().model(Generation.Models.QWEN_MAX).messages(msgManager.get())
                        .resultFormat(QwenParam.ResultFormat.MESSAGE)
                        .topP(0.8)
                        .enableSearch(true)
                        .build();

        GenerationResult result = gen.call(param);
        msgManager.add(result);
        String back_text = result.getOutput().getChoices().get(0).getMessage().getContent();
        List<String> return_text = new ArrayList<String>(Arrays.asList(back_text.split("\n")));
        System.out.println(return_text);

        return return_text;
    }

    @RequestMapping(value = "/generateKeywords")
    public void downloadKeywords(HttpServletResponse response) throws IOException, NoApiKeyException, InputRequiredException {
        String meeting_path = "C:/Users/Administrator/Desktop/files/";
        String meeting_name = "原始文字.docx";
        List<String> origin_text = readTxtFile(meeting_path, meeting_name);

        Generation gen = new Generation();
        Constants.apiKey="sk-df302754ac424760b1961b64dab5020d";

        String prompt_path = "C:/Users/Administrator/Desktop/formatFiles/";
        String prompt_name = "关键词提示.docx";
        String send_text = String.join(" ", origin_text);
        String prompt_text = String.join(" ", readTxtFile(prompt_path, prompt_name));
        System.out.println(prompt_text);
        MessageManager msgManager = new MessageManager(10);
        Message systemMsg =
                Message.builder().role(Role.SYSTEM.getValue()).content(prompt_text).build();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(send_text).build();
        msgManager.add(systemMsg);
        msgManager.add(userMsg);
        QwenParam param =
                QwenParam.builder().model(Generation.Models.QWEN_MAX).messages(msgManager.get())
                        .resultFormat(QwenParam.ResultFormat.MESSAGE)
                        .topP(0.8)
                        .enableSearch(true)
                        .build();

        GenerationResult result = gen.call(param);
        msgManager.add(result);
        String back_text = result.getOutput().getChoices().get(0).getMessage().getContent();
        List<String> new_text = new ArrayList<String>(Arrays.asList(back_text.split("\n")));

        String output_path = "C:/Users/Administrator/Desktop/files/";
        String output_name = "关键词.docx";
        XWPFDocument doc = new XWPFDocument();

        for (String s : new_text) {
            XWPFParagraph para = doc.createParagraph();
            XWPFRun run = para.createRun();
            para.setAlignment(ParagraphAlignment.LEFT);
            run.setText(s);
        }
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        FileOutputStream fileOutputStream = null;
        doc.write(bao);
        try {
            fileOutputStream = new FileOutputStream(output_path + output_name);
            fileOutputStream.write(bao.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.setHeader("Content-Disposition", "attachment;filename=" + output_name);
        response.setHeader("resource-filename", output_name);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // 写入响应流
        ServletOutputStream outputStream = response.getOutputStream();
        for (String line : new_text) {
            outputStream.write(line.getBytes());
            outputStream.write('\n');
            outputStream.flush();
        }

    }

    @RequestMapping(value = "/generateAbstract")
    public void downloadKeywordsAbstract(@RequestParam("keywords") String [] keywords, HttpServletResponse response) throws IOException, NoApiKeyException, InputRequiredException {
        String meeting_path = "C:/Users/Administrator/Desktop/files/";
        String meeting_name = "原始文字.docx";
        List<String> origin_text = readTxtFile(meeting_path, meeting_name);

        Generation gen = new Generation();
        Constants.apiKey="sk-df302754ac424760b1961b64dab5020d";

        List<String> abstractPrompt = new ArrayList<>();
        abstractPrompt.add(0, "你是一个会议纪要小助手，你可以包含如下关键词生成一份会议纪要吗？");
        for (String keyword : keywords) {
            abstractPrompt.add(keyword);
        }

        String send_text = String.join(" ", origin_text);
        String prompt_text = String.join(" ", abstractPrompt);
        System.out.println(prompt_text);
        MessageManager msgManager = new MessageManager(10);
        Message systemMsg =
                Message.builder().role(Role.SYSTEM.getValue()).content(prompt_text).build();
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(send_text).build();
        msgManager.add(systemMsg);
        msgManager.add(userMsg);
        QwenParam param =
                QwenParam.builder().model(Generation.Models.QWEN_MAX).messages(msgManager.get())
                        .resultFormat(QwenParam.ResultFormat.MESSAGE)
                        .topP(0.8)
                        .enableSearch(true)
                        .build();

        GenerationResult result = gen.call(param);
        msgManager.add(result);
        String back_text = result.getOutput().getChoices().get(0).getMessage().getContent();
        List<String> chinese_text = new ArrayList<String>(Arrays.asList(back_text.split("\n")));

        String output_path = "C:/Users/Administrator/Desktop/files/";
        String output_name = "关键词摘要.docx";

        System.out.println("中文纪要大小：" + chinese_text.size());

        boolean translate_englist = false;
        boolean translate_portuguese = false;

        String languages_path = "C:/Users/Administrator/Desktop/formatFiles/";
        String languages_name = "语言.docx";

        List<String> languages = readTxtFile(languages_path, languages_name);

        for (String language : languages) {
            if (language.equals("英语")) {
                translate_englist = true;
            }
            if (language.equals("葡语")) {
                translate_portuguese = true;
            }
        }

        List<String> english_text = new ArrayList<>();
        List<String> portuguese_text = new ArrayList<>();

        if (translate_englist) {
            english_text = translate(chinese_text, "请遵从一样的格式帮我翻译成英文，注意换行");
            System.out.println("英文纪要大小：" + english_text.size());
        }
        if (translate_portuguese) {
            portuguese_text = translate(chinese_text, "请遵从一样的格式帮我翻译成葡语，注意换行");
            System.out.println("葡语纪要大小：" + portuguese_text.size());
        }

        List<String> new_text = new ArrayList<>();
        for (int i = 0; i < chinese_text.size(); i++) {
            new_text.add(chinese_text.get(i));
            if (chinese_text.get(i) != "") {
                if (translate_englist) {
                    new_text.add(english_text.get(i));
                }
                if (translate_portuguese) {
                    new_text.add(portuguese_text.get(i));
                }
            }
        }
        XWPFDocument doc = new XWPFDocument();

        for (String s : new_text) {
            XWPFParagraph para = doc.createParagraph();
            XWPFRun run = para.createRun();
            para.setAlignment(ParagraphAlignment.LEFT);
            run.setText(s);
        }
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        FileOutputStream fileOutputStream = null;
        doc.write(bao);
        try {
            fileOutputStream = new FileOutputStream(output_path + output_name);
            fileOutputStream.write(bao.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.setHeader("Content-Disposition", "attachment;filename=" + output_name);
        response.setHeader("resource-filename", output_name);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // 写入响应流
        ServletOutputStream outputStream = response.getOutputStream();
        for (String line : new_text) {
            outputStream.write(line.getBytes());
            outputStream.write('\n');
            outputStream.flush();
        }
    }
}

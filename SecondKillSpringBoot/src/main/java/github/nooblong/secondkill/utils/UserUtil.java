package github.nooblong.secondkill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import github.nooblong.secondkill.entity.User;
import github.nooblong.secondkill.mapper.UserMapper;
import github.nooblong.secondkill.service.IUserService;
import github.nooblong.secondkill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserUtil {

    @Autowired
    UserMapper userMapper;

    public void createUset(int count) throws Exception {
        List<User> users = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(1300000000L + i);
            user.setNickname("userg" + i);
            user.setSlat("salt");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSlat()));
            user.setLoginCount(1);
            users.add(user);
        }
        for (User user : users) {
            userMapper.insert(user);
        }
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("C:/Users/lyl/Desktop/config.txt");
        if (file.exists())
            file.delete();
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream out = connection.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" +
                    MD5Util.inputPassToFromPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len= inputStream.read(buff)) >= 0){
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = bout.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            RespBean respBean = objectMapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObj();
            System.out.println("create userTicket: " + userTicket);
            String row = user.getId()+","+userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
        }
        raf.close();

    }

}

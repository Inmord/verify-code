package com.spnetwork.verifycode.service;

import com.spnetwork.verifycode.dto.CoordinateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author aiker
 * @implSpec Nu bug no errors
 * @since 2022/12/3
 */
@Slf4j
@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {

    public static final String pathStr = "F:\\workspace\\images\\";
    @Value("${verify.code.image}")
    private String imagePath;
    @Value("${verify.code.txt}")
    private String txtPath;

    @Override
    public Map<String, Object> getOne() {
        Path rootPath = Paths.get(imagePath);
        List<Path> imagesList = null;
        try {
            imagesList = Files.list(rootPath).filter(path -> {
                String fileName = path.toString();
                return fileName.endsWith("jpg") || fileName.endsWith(".png");
            }).collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        HashMap<String, Object> ans = new HashMap<>();
        if (imagesList == null || imagesList.isEmpty()) {
            return this.handleMsg(ans, 400011, "暂无验证码图片信息配置");
        }
        int imageIndex = ThreadLocalRandom.current().nextInt(imagesList.size());
        Path path = imagesList.get(imageIndex);
        log.info("获取图片名成功...{}", path);
        try {
            String[] split = path.getFileName().toString().split("\\.");
            String id = split[0];
            byte[] imageByte = Files.readAllBytes(path);
            String imageBase64 = Base64Utils.encodeToString(imageByte);
            HashMap<String, Object> specialMap = new HashMap<>();

            Path pathText = Paths.get(txtPath + id + ".txt");
            List<String> txtAllLines;
            try {
                txtAllLines = Files.readAllLines(pathText);
                if (txtAllLines.size() < 2) {
                    throw new IOException("您的图片格式有误");
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return this.handleMsg(ans, 400001, "错误的验证图片信息");
            }

            specialMap.put("image", imageBase64);
            specialMap.put("length", txtAllLines.size());
            specialMap.put("imageId", id);
            ans.put("data", specialMap);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return this.handleMsg(ans, 200, "OK");
    }

    @Override
    public Map<String, Object> verifyCode(String id, CoordinateDTO[] dtoArr) {
        if (id == null || dtoArr == null || dtoArr.length < 2) return null;
        HashMap<String, Object> ans = new HashMap<>();
        // 读取id图片信息
        Path path = Paths.get(txtPath + id + ".txt");
        List<String> txtAllLines;
        try {
            txtAllLines = Files.readAllLines(path);
            if (txtAllLines.size() < 2) {
                throw new IOException("您的图片格式有误");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return this.handleMsg(ans, 400001, "错误的验证图片信息");
        }
        txtAllLines = txtAllLines.stream().filter(it -> !"".equals(it)).collect(Collectors.toList());
        if (dtoArr.length != txtAllLines.size()) {
            return this.handleMsg(ans, 400002, "验证信息有误");
        }
        for (int i = 0; i < txtAllLines.size(); i++) {
            String txtAllLine = txtAllLines.get(i);
            String[] lineArr = txtAllLine.split(" ");
            CoordinateDTO coordinateDTO = dtoArr[i];
            BigDecimal x1 = coordinateDTO.getX1();
            BigDecimal y1 = coordinateDTO.getY1();

            // 位置
            BigDecimal dbX1 = new BigDecimal(lineArr[1]);
            BigDecimal dbX2 = new BigDecimal(lineArr[3]);
            BigDecimal dbY2 = new BigDecimal(lineArr[4]);
            BigDecimal dbX2Core = dbX2.divide(new BigDecimal(2)).setScale(4, RoundingMode.HALF_UP);
            BigDecimal dbY2Core = dbY2.divide(new BigDecimal(2)).setScale(4, RoundingMode.HALF_UP);;
            if (dbX1.subtract(dbX2Core).compareTo(x1) > -1 || dbX1.add(dbX2Core).compareTo(x1) < 1) {
                return this.handleMsg(ans, 400004, "您点击的图标位置有误，请按照验证码的图标顺序依次点击");
            }
            BigDecimal dbY1 = new BigDecimal(lineArr[2]);
            if (dbY1.subtract(dbY2Core).compareTo(y1) > -1 || dbY1.add(dbY2Core).compareTo(y1) < 1) {
                return this.handleMsg(ans, 400006, "您点击的图标位置有误，请按照验证码的图标顺序依次点击");
            }
        }
        return this.handleMsg(ans, 200, "OK");
    }

    private Map<String, Object> handleMsg(HashMap<String, Object> ans, Integer code, String msg) {
        ans.put("code", code);
        ans.put("msg", msg);
        return ans;
    }

}

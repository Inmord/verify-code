package com.spnetwork.verifycode.service;

import com.spnetwork.verifycode.dto.CoordinateDTO;
import lombok.extern.slf4j.Slf4j;
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

    private static final BigDecimal coordinateCore = new BigDecimal("0.2000");

    @Override
    public Map<String, Object> getOne() {
        String pathStr = "F:\\workspace\\images\\";
        Path rootPath = Paths.get(pathStr);
        List<Path> imagesList = null;
        try {
            imagesList = Files.list(rootPath).filter(path -> {
                String fileName = path.toString();
                return fileName.endsWith("jpg") || fileName.endsWith(".png");
            }).toList();
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

            Path pathText = Paths.get(pathStr + id + ".txt");
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
            specialMap.put("length", txtAllLines.size() - 1);
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
        Path path = Paths.get("F:\\workspace\\images\\" + id + ".txt");
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
//            Integer iconId = coordinateDTO.getIconId();
            BigDecimal x1 = coordinateDTO.getX1();
//            BigDecimal x2 = coordinateDTO.getX2();
            BigDecimal y1 = coordinateDTO.getY1();
//            BigDecimal y2 = coordinateDTO.getY2();

//            if (Integer.parseInt(lineArr[0]) != iconId) {
//                return this.handleMsg(ans, 400003, "验证信息有误-图标异常");
//            }

            // 位置
            BigDecimal dbX1 = new BigDecimal(lineArr[1]);
            BigDecimal dbX2 = new BigDecimal(lineArr[3]);
            BigDecimal dbY2 = new BigDecimal(lineArr[4]);
            BigDecimal dbX2Core = dbX2.divide(new BigDecimal(2)).setScale(4, RoundingMode.HALF_UP);
            BigDecimal dbY2Core = dbY2.divide(new BigDecimal(2)).setScale(4, RoundingMode.HALF_UP);;
            if (dbX1.subtract(dbX2Core).compareTo(x1) > -1 || dbX1.add(dbX2Core).compareTo(x1) < 1) {
                return this.handleMsg(ans, 400004, "验证信息有误-" + i + "-x1异常");
            }
//            BigDecimal dbX2 = new BigDecimal(lineArr[2]);
//            if (dbX2.subtract(coordinateCore).compareTo(x2) > -1 || dbX2.add(coordinateCore).compareTo(x2) < 1) {
//                return this.handleMsg(ans, 400005, "验证信息有误-x2异常");
//            }
            BigDecimal dbY1 = new BigDecimal(lineArr[2]);
            if (dbY1.subtract(dbY2Core).compareTo(y1) > -1 || dbY1.add(dbY2Core).compareTo(y1) < 1) {
                return this.handleMsg(ans, 400006, "验证信息有误-" + i + "-y1异常");
            }
//            BigDecimal dbY2 = new BigDecimal(lineArr[4]);
//            if (dbY2.subtract(coordinateCore).compareTo(y2) > -1 || dbY2.add(coordinateCore).compareTo(y2) < 1) {
//                return this.handleMsg(ans, 400007, "验证信息有误-y2异常");
//            }
        }
        return this.handleMsg(ans, 200, "OK");
    }

    private Map<String, Object> handleMsg(HashMap<String, Object> ans, Integer code, String msg) {
        ans.put("code", code);
        ans.put("msg", msg);
        return ans;
    }

}

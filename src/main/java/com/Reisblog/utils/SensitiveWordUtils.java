package com.Reisblog.utils;

import com.Reisblog.entity.SensitiveWord;
import com.Reisblog.service.SensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

// TODO 敏感词处理
@Component
public class SensitiveWordUtils {

    @Autowired
    private SensitiveWordService sensitiveWordService;

    private static List<String> sensitiveWords;

    @PostConstruct
    public void init() {
        // 启动时加载所有屏蔽词到内存
        List<SensitiveWord> words = sensitiveWordService.list();
        sensitiveWords = words.stream().map(SensitiveWord::getWord).collect(Collectors.toList());
    }

    /**
     * 检查内容是否包含屏蔽词
     * @param text 待检查文本
     * @return true 表示包含屏蔽词
     */
    public static boolean containsSensitiveWord(String text) {
        if (sensitiveWords == null || sensitiveWords.isEmpty()) {
            return false;
        }
        for (String word : sensitiveWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 刷新屏蔽词列表（管理员增删改后调用）
     */
    public static void refresh(List<String> newWords) {
        sensitiveWords = newWords;
    }

}

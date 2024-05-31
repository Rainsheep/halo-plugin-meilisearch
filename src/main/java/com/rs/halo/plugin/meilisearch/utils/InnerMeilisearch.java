package com.rs.halo.plugin.meilisearch.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.rs.halo.plugin.meilisearch.config.MeilisearchSetting;
import com.rs.halo.plugin.meilisearch.event.ConfigUpdatedEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InnerMeilisearch {

    public static final String MEILISEARCH_VERSION = "1.6.1";

    private Process currentProcess;

    private boolean enable(ConfigUpdatedEvent event) {
        return MeilisearchSetting.innerServiceEnable;
    }

    private boolean disable(ConfigUpdatedEvent event) {
        return !enable(event);
    }

    @EventListener
    private void dealInnerMeilisearch(ConfigUpdatedEvent event) {
        if (disable(event)) {
            log.info("inner meilisearch is disabled");
            // todo 关闭现有进程
            return;
        }
        // todo 有 meilisearch 进程则直接跳过
        startMeilisearch();
    }

    // 寻找 meilisearch 进程
    private void findMeilisearchProcess() {
        // todo
    }

    private void startMeilisearch() {
        ThreadUtil.execAsync(() -> {
            try {
                // todo 支持 windows
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    downloadMeilisearchForWindows();
                } else {
                    downloadMeilisearchForUnix();
                    startMeilisearchServerForUnit();
                }
            } catch (IOException | InterruptedException e) {
                log.error("Failed to start meilisearch", e);
            }
        });
    }

    private void downloadMeilisearchForWindows() {

    }

    private void downloadMeilisearchForUnix() throws IOException, InterruptedException {
        // 复制 install.sh 至  ${workDir}/services/meilisearch
        String installShellPath = getMeilisearchPath() + "/install.sh";
        URL installShellUrl =
            InnerMeilisearch.class.getClassLoader().getResource("shell/install.sh");
        FileUtil.copy(installShellUrl.getFile(), installShellPath, true);
        ProcessBuilder processBuilder = new ProcessBuilder();
        // 运行安装脚本(会下载或使用本地的 meilisearch 二进制文件并删除旧版二进制文件)
        processBuilder.command("sh", installShellPath, MEILISEARCH_VERSION, getMeilisearchPath());
        Process process = processBuilder.start();
        // 读取shell脚本的输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log.info(line);
        }
        int code = process.waitFor();
        if (code != 0) {
            throw new IOException("download meilisearch error, code: " + code);
        }
    }

    private void startMeilisearchServerForUnit() throws IOException, InterruptedException {
        log.info("start meilisearch server");
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(getMeilisearchPath() + "/" + MEILISEARCH_VERSION + "/meilisearch",
            "--master-key=" + MeilisearchSetting.DEFAULT_MASTER_KEY, "--no-analytics",
            "--env=production");
        Process process = processBuilder.start();
        currentProcess = process;
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            log.info(line);
        }
        int code = process.waitFor();
        if (code != 0) {
            throw new IOException("run meilisearch error, code: " + code);
        }
    }

    private String getMeilisearchPath() {
        String workDir = System.getProperty("LOG_FILE").split("/logs/", 2)[0];
        return workDir + "/services/meilisearch";
    }
}

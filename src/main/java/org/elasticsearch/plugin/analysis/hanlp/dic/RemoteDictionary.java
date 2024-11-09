package org.elasticsearch.plugin.analysis.hanlp.dic;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.dictionary.other.CharTable;
import com.hankcs.hanlp.utility.LexiconUtility;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.SpecialPermission;
import org.elasticsearch.core.Tuple;
import org.elasticsearch.plugin.analysis.hanlp.cfg.Configuration;

import java.io.*;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RemoteDictionary {
    //
    private static final Logger logger = LogManager.getLogger(RemoteDictionary.class);
    //
    private final static String FILE_NAME = "hanlp-remote.xml";
    // run every 60 sec
    private final static ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
    // key in hanlp-remote.xml
    private final static String EXT_DICT = "remote_ext_dict";
    // key in hanlp-remote.xml
    private final static String EXT_STOP = "remote_ext_stopwords";
    // instance
    private static RemoteDictionary singleton;
    //
    private Path conf_dir;
    // hanlp-remote.xml properties
    private Properties props;

    private RemoteDictionary(Configuration cfg) {
        this.props = new Properties();
        this.conf_dir = cfg.getConfDir();
        Path configFile = conf_dir.resolve(FILE_NAME);

        InputStream input = null;
        try {
            logger.info("try load config from {}", configFile);
            input = new FileInputStream(configFile.toFile());
        } catch (FileNotFoundException e) {
            conf_dir = cfg.getConfigInPluginDir();
            configFile = conf_dir.resolve(FILE_NAME);
            try {
                logger.info("try load config from {}", configFile);
                input = new FileInputStream(configFile.toFile());
            } catch (FileNotFoundException ex) {
                // We should report origin exception
                logger.error("hanlp-analyzer", e);
            }
        }
        if (input != null) {
            try {
                props.loadFromXML(input);
            } catch (IOException e) {
                logger.error("hanlp-analyzer", e);
            }
        }
    }

    public static synchronized void initial(Configuration cfg) {
        if (singleton == null) {
            synchronized (RemoteDictionary.class) {
                if (singleton == null) {

                    singleton = new RemoteDictionary(cfg);

                    // 建立监控线程
                    for (String location : singleton.getExtDictionarys()) {
                        // 10 秒是初始延迟可以修改的 60是间隔时间 单位秒
                        pool.scheduleAtFixedRate(new RemoteMonitor(location), 10, 60, TimeUnit.SECONDS);
                    }
                    for (String location : singleton.getExtStopWordDictionarys()) {
                        pool.scheduleAtFixedRate(new RemoteMonitor(location), 10, 60, TimeUnit.SECONDS);
                    }
                }
            }
        }
    }

    public static RemoteDictionary getSingleton() {
        if (singleton == null) {
            throw new IllegalStateException("hanlp dict has not been initialized yet, please call initial method first.");
        }
        return singleton;
    }

    // 从远程服务器上下载自定义词条
    private static List<String> getWords(String location) {
        SpecialPermission.check();
        return AccessController.doPrivileged((PrivilegedAction<List<String>>) () -> {
            return getWordsUnprivileged(location);
        });
    }

    // 从远程服务器上下载自定义词条
    private static List<String> getWordsUnprivileged(String location) {

        List<String> buffer = new ArrayList<String>();
        RequestConfig rc = RequestConfig.custom().setConnectionRequestTimeout(10 * 1000).setConnectTimeout(10 * 1000)
                .setSocketTimeout(60 * 1000).build();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;
        BufferedReader in;
        HttpGet get = new HttpGet(location);
        get.setConfig(rc);
        try {
            response = httpclient.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {

                String charset = "UTF-8";
                // 获取编码，默认为utf-8
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    Header contentType = entity.getContentType();
                    if (contentType != null && contentType.getValue() != null) {
                        String typeValue = contentType.getValue();
                        if (typeValue != null && typeValue.contains("charset=")) {
                            charset = typeValue.substring(typeValue.lastIndexOf("=") + 1);
                        }
                    }

                    if (entity.getContentLength() > 0 || entity.isChunked()) {
                        in = new BufferedReader(new InputStreamReader(entity.getContent(), charset));
                        String line;
                        while ((line = in.readLine()) != null) {
                            buffer.add(line);
                        }
                        in.close();
                        response.close();
                        return buffer;
                    }
                }
            }
            response.close();
        } catch (IllegalStateException | IOException e) {
            logger.error("getWords {} error", e, location);
        }
        return buffer;
    }

    // 1. 将远程自定义词条数据加入到CustomDictionary
    private void loadExtDict() {
        List<String> extDictFiles = getExtDictionarys();
        for (String location : extDictFiles) {
            logger.info("[Dict Loading] " + location);

            //
            Tuple<String, Nature> defaultInfo = analysisDefaultInfo(location);
            List<String> lists = getWords(defaultInfo.v1());
            // 如果找不到扩展的字典，则忽略
            if (lists == null) {
                logger.error("[Dict Loading] " + location + " load failed");
                continue;
            }
            for (String line : lists) {
                if (line != null && !"".equals(line.trim())) {
                    //
                    logger.info(line);

                    String[] lineArr = line.split("\\s");
                    String word = lineArr[0];

                    if (HanLP.Config.Normalization) {
                        word = CharTable.convert(word);
                    }

                    // 是否会造成一些不稳定、诡异的现象？
                    CustomDictionary.insert(word, analysisNatureWithFrequency(defaultInfo.v2(), lineArr));
                }
            }
        }

    }

    // 2. 加载用户扩展的停止词词典
    private void loadExtStopWordDict() {
        // 加载远程停用词典
        List<String> dictFiles = getExtStopWordDictionarys();
        for (String location : dictFiles) {
            logger.info("[Dict Loading] " + location);
            List<String> lists = getWords(location);
            // 如果找不到扩展的字典，则忽略
            if (lists == null) {
                logger.error("[Dict Loading] " + location + " load failed");
                continue;
            }
            for (String line : lists) {
                if (line != null && !"".equals(line.trim())) {
                    // 加载远程词典数据到主内存中
                    logger.info(line);
                    CoreStopWordDictionary.add(line);
                }
            }
        }

    }

    private String getProperty(String key) {
        if (props != null) {
            return props.getProperty(key);
        }
        return null;
    }

    private List<String> getExtDictionarys() {
        List<String> extDictFiles = new ArrayList<String>(2);
        String extDictCfg = getProperty(EXT_DICT);
        if (extDictCfg != null) {
            // 根据;拆分URL
            String[] filePaths = extDictCfg.split(";");
            for (String filePath : filePaths) {
                if (filePath != null && !"".equals(filePath.trim())) {
                    extDictFiles.add(filePath);

                }
            }
        }
        return extDictFiles;
    }

    private List<String> getExtStopWordDictionarys() {
        List<String> extStopWordDictFiles = new ArrayList<String>(2);
        String extStopWordDictCfg = getProperty(EXT_STOP);
        if (extStopWordDictCfg != null) {
            // 根据;拆分URL
            String[] filePaths = extStopWordDictCfg.split(";");
            for (String filePath : filePaths) {
                if (filePath != null && !"".equals(filePath.trim())) {
                    extStopWordDictFiles.add(filePath);

                }
            }
        }
        return extStopWordDictFiles;
    }

    void load() {
        logger.info("start to reload hanlp dict.");
        loadExtDict();
        loadExtStopWordDict();
        logger.info("reload hanlp dict finished.");
    }

    /**
     * 解析默认信息
     *
     * @param location 配置路径
     * @return 返回{路径, 默认词性}
     */
    private Tuple<String, Nature> analysisDefaultInfo(String location) {
        Nature defaultNature = Nature.n;
        String path = location;
        int cut = location.indexOf(' ');
        if (cut > 0) {
            // 有默认词性
            String nature = location.substring(cut + 1);
            path = location.substring(0, cut);
            defaultNature = LexiconUtility.convertStringToNature(nature);
        }
        return Tuple.tuple(path, defaultNature);
    }

    /**
     * 分析词性和频次
     *
     * @param defaultNature 默认词性
     * @param param         行数据
     * @return 返回[单词] [词性A] [A的频次] [词性B] [B的频次] ...
     */
    private String analysisNatureWithFrequency(Nature defaultNature, String[] param) {
        int natureCount = (param.length - 1) / 2;
        StringBuilder builder = new StringBuilder();
        if (natureCount == 0) {
            builder.append(defaultNature).append(" ").append(1000);
        } else {
            for (int i = 0; i < natureCount; ++i) {
                Nature nature = LexiconUtility.convertStringToNature(param[1 + 2 * i]);
                int frequency = Integer.parseInt(param[2 + 2 * i]);
                builder.append(nature).append(" ").append(frequency);
                if (i != natureCount - 1) {
                    builder.append(" ");
                }
            }
        }
        return builder.toString();
    }
}

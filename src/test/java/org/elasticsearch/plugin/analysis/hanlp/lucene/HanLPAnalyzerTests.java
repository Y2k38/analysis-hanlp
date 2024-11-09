package org.elasticsearch.plugin.analysis.hanlp.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.elasticsearch.plugin.analysis.hanlp.HanLPAnalyzerProvider;
import org.elasticsearch.plugin.analysis.hanlp.TestUtils;
import org.elasticsearch.plugin.analysis.hanlp.cfg.Configuration;
import org.elasticsearch.plugin.analysis.hanlp.core.HanLPType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HanLPAnalyzerTests {
    static String[] tokenize(HanLPType hanLPType, Configuration configuration, String s) {
        ArrayList<String> tokens = new ArrayList<>();
        try (Analyzer analyzer = HanLPAnalyzerProvider.getAnalyzer(hanLPType, configuration)) {
            TokenStream tokenStream = analyzer.tokenStream("text", s);
            tokenStream.reset();

            while (tokenStream.incrementToken()) {
                CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
                int len = offsetAttribute.endOffset() - offsetAttribute.startOffset();
                char[] chars = new char[len];
                System.arraycopy(charTermAttribute.buffer(), 0, chars, 0, len);
                tokens.add(new String(chars));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return tokens.toArray(new String[0]);
    }

    @Test
    public void tokenize_hanlp_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        List<String> values = Arrays.asList(tokenize(HanLPType.HANLP, cfg, "美国阿拉斯加州发生8.0级地震"));
        assert values.size() >= 6;
        assert values.contains("美国");
        assert values.contains("阿拉斯加州");
        assert values.contains("发生");
        assert values.contains("8.0");
        assert values.contains("级");
        assert values.contains("地震");
    }

    @Test
    public void tokenize_hanlp_standard_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        List<String> values = Arrays.asList(tokenize(HanLPType.STANDARD, cfg, "商品和服务"));
        assert values.size() >= 3;
        assert values.contains("商品");
        assert values.contains("和");
        assert values.contains("服务");
    }

    @Test
    public void tokenize_hanlp_index_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        List<String> values = Arrays.asList(tokenize(HanLPType.INDEX, cfg, "主副食品"));
        assert values.size() >= 5;
        assert values.contains("主副食品");
        assert values.contains("主副食");
        assert values.contains("副食品");
        assert values.contains("副食");
        assert values.contains("食品");
    }

    @Test
    public void tokenize_hanlp_nlp_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        List<String> values = Arrays.asList(tokenize(HanLPType.NLP, cfg, "我新造一个词叫幻想乡你能识别并标注正确词性吗？"));
        assert values.size() >= 15;
        assert values.contains("我");
        assert values.contains("新");
        assert values.contains("造");
        assert values.contains("一个");
        assert values.contains("词叫");
        assert values.contains("幻想乡");
        assert values.contains("你");
        assert values.contains("能");
        assert values.contains("识别");
        assert values.contains("并");
        assert values.contains("标注");
        assert values.contains("正确");
        assert values.contains("词性");
        assert values.contains("吗");
        assert values.contains("？");
    }

    @Test
    public void tokenize_hanlp_crf_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        List<String> values = Arrays.asList(tokenize(HanLPType.CRF, cfg, "微软公司於1975年由比爾·蓋茲和保羅·艾倫創立，18年啟動以智慧雲端、前端為導向的大改組。"));
        assert values.size() >= 22;
        assert values.contains("微软公司");
        assert values.contains("於");
        assert values.contains("1975年");
        assert values.contains("由");
        assert values.contains("比爾·蓋茲");
        assert values.contains("和");
        assert values.contains("保羅·艾倫");
        assert values.contains("創立");
        assert values.contains("，");
        assert values.contains("18年");
        assert values.contains("啟動");
        assert values.contains("以");
        assert values.contains("智慧");
        assert values.contains("雲端");
        assert values.contains("、");
        assert values.contains("前端");
        assert values.contains("為");
        assert values.contains("導向");
        assert values.contains("的");
        assert values.contains("大");
        assert values.contains("改組");
        assert values.contains("。");
    }

    @Test
    public void tokenize_hanlp_n_short_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        List<String> values = Arrays.asList(tokenize(HanLPType.N_SHORT, cfg, "刘喜杰石国祥会见吴亚琴先进事迹报告团成员"));
        assert values.size() >= 8;
        assert values.contains("刘喜杰");
        assert values.contains("石国祥");
        assert values.contains("会见");
        assert values.contains("吴亚琴");
        assert values.contains("先进");
        assert values.contains("事迹");
        assert values.contains("报告团");
        assert values.contains("成员");
    }

    @Test
    public void tokenize_hanlp_dijkstra_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        List<String> values = Arrays.asList(tokenize(HanLPType.DIJKSTRA, cfg, "今天，刘志军案的关键人物,山西女商人丁书苗在市二中院出庭受审。"));
        assert values.size() >= 19;
        assert values.contains("今天");
        assert values.contains("，");
        assert values.contains("刘志军");
        assert values.contains("案");
        assert values.contains("的");
        assert values.contains("关键");
        assert values.contains("人物");
        assert values.contains(",");
        assert values.contains("山西");
        assert values.contains("女");
        assert values.contains("商人");
        assert values.contains("丁书苗");
        assert values.contains("在");
        assert values.contains("市");
        assert values.contains("二");
        assert values.contains("中院");
        assert values.contains("出庭");
        assert values.contains("受审");
        assert values.contains("。");
    }

    @Test
    public void tokenize_hanlp_speed_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub();
        List<String> values = Arrays.asList(tokenize(HanLPType.SPEED, cfg, "江西鄱阳湖干枯，中国最大淡水湖变成大草原"));
        assert values.size() >= 9;
        assert values.contains("江西");
        assert values.contains("鄱阳湖");
        assert values.contains("干枯");
        assert values.contains("，");
        assert values.contains("中国");
        assert values.contains("最大");
        assert values.contains("淡水湖");
        assert values.contains("变成");
        assert values.contains("大草原");
    }
}

package org.elasticsearch.plugin.analysis.hanlp.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.elasticsearch.plugin.analysis.hanlp.TestUtils;
import org.elasticsearch.plugin.analysis.hanlp.cfg.Configuration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HanLPAnalyzerTests {
    static String[] tokenize(Configuration configuration, String s) {
        ArrayList<String> tokens = new ArrayList<>();
        try (HanLPAnalyzer hanLPAnalyzer = new HanLPAnalyzer(configuration)) {
            TokenStream tokenStream = hanLPAnalyzer.tokenStream("text", s);
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
        List<String> values = Arrays.asList(tokenize(cfg, "美国阿拉斯加州发生8.0级地震"));
        assert values.size() >= 6;
        assert values.contains("美国");
        assert values.contains("阿拉斯加州");
        assert values.contains("发生");
        assert values.contains("8.0");
        assert values.contains("级");
        assert values.contains("地震");
    }
}

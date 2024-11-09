package org.elasticsearch.plugin.analysis.hanlp;

import com.hankcs.hanlp.utility.Predefine;
import org.elasticsearch.plugin.analysis.hanlp.cfg.Configuration;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class TestUtils {
    public static Configuration createFakeConfigurationSub() {
        FakeConfigurationSub configurationSub = new FakeConfigurationSub();

        Predefine.HANLP_PROPERTIES_PATH = FakeConfigurationSub.getConfigDir()
                .resolve("hanlp.properties").toString();

        return configurationSub;
    }

    static class FakeConfigurationSub extends Configuration {
        public FakeConfigurationSub() {
        }

        private static Path getConfigDir() {
            String projectRoot = new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath();
            return new File(projectRoot, "analysis-hanlp/src/test/config").toPath();
        }

        @Override
        public Path getConfDir() {
            return getConfigDir();
        }

        @Override
        public Path getConfigInPluginDir() {
            return getConfigDir();
        }

        @Override
        public Path getPath(String first, String... more) {
            return FileSystems.getDefault().getPath(first, more);
        }
    }
}

import me.clickism.configured.Config;
import me.clickism.configured.ConfigOption;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ConfigTest {

    private static final Config CONFIG = Config.ofYaml("test/lol/config.yml");

    private static final ConfigOption<Boolean> ENABLE_CLAIMS = CONFIG.register(
            ConfigOption.of("enable_claims", true)
                    .description("""
                            Enable or disable claiming villagers.
                            Default: true
                            """)
    );

    private static final ConfigOption<List<String>> WHITELIST = CONFIG.register(
            ConfigOption.of("whitelisted_mobs", List.<String>of())
                    .description("""
                            Whitelist of mobs that can be claimed.
                            Default: []
                            """)
    );

    @Test
    public void testSave() {
        CONFIG.save();
    }
}

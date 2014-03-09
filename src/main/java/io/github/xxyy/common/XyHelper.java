package io.github.xxyy.common;

import io.github.xxyy.common.localisation.XycLocale;
import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.common.sql.XycSqlDataManager;
import io.github.xxyy.common.xyplugin.AbstractXyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

/**
 * Class that deals with initialisation of XYC and storing plugins.
 *
 * @author xxyy98
 */
public class XyHelper { //TODO logger

    private static final File cfgFile = new File("plugins/XYC", "xyc.cfg.yml");
    private static YamlConfiguration xycConfiguration = YamlConfiguration.loadConfiguration(XyHelper.cfgFile);
    /**
     * Default language used by {@link io.github.xxyy.common.localisation.LangHelper}.
     */
    public static String defaultLang = "de_AT";
    /**
     * XYC primary chat color. Whatever.
     */
    public static String priChatCol = "§7";
    /**
     * Chat color used for warnings.
     */
    public static String warChatCol = "§8";
    /**
     * Chat color used for code or commands.
     */
    public static String codeChatCol = "§3";
    private static XycLocale locale;
    @Deprecated
    private static boolean isSql = true;

    /**
     * Common {@link SafeSql} if you don't make your own using SqlXyPlugin.
     */
    public static final SafeSql commonSql = new SafeSql(new XycSqlDataManager());

    /**
     * Gets the XYC config as {@link YamlConfiguration}.
     */
    public static YamlConfiguration getCfg() {
        return XyHelper.xycConfiguration;
    }

    /**
     * Gets the internal locale storage.
     */
    public static XycLocale getLocale() {
        return XyHelper.locale;
    }

    protected static void initialise() {

        //config
        XyHelper.xycConfiguration.options().header("XYC....use valid YAML!");
        XyHelper.xycConfiguration.options().copyHeader(true);
        XyHelper.xycConfiguration.options().copyDefaults(true);
        XyHelper.xycConfiguration.addDefault("sql.user", "root");
        XyHelper.xycConfiguration.addDefault("sql.db", "minecraft");
        XyHelper.xycConfiguration.addDefault("sql.password", "");
        XyHelper.xycConfiguration.addDefault("sql.host", "jdbc:mysql://localhost:3306/");

        //locale
        XyHelper.locale = new XycLocale();
        XyHelper.priChatCol = XycLocale.getString("XYC-pricol", "CONSOLE");
        XyHelper.warChatCol = XycLocale.getString("XYC-warningcol", "CONSOLE");
        XyHelper.codeChatCol = XycLocale.getString("XYC-codecol", "CONSOLE");

//        XyHelper.isSql=ev.isSql();
        Bukkit.getConsoleSender().sendMessage("§8[XYC] Initialised XYC!");
    }

    /**
     * @deprecated Use {@link AbstractXyPlugin#getInstances()}.
     */
    @Deprecated
    public static List<AbstractXyPlugin> getPlugins() {
        return AbstractXyPlugin.getInstances();
    }
}

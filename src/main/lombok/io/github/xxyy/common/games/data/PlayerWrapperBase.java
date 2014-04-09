package io.github.xxyy.common.games.data;

import io.github.xxyy.common.games.GameLib;
import io.github.xxyy.common.lib.com.mojang.api.profiles.HttpProfileRepository;
import io.github.xxyy.common.sql.QueryResult;
import io.github.xxyy.common.sql.SafeSql;
import io.github.xxyy.common.sql.builder.*;
import io.github.xxyy.common.sql.builder.annotation.SqlNumberCache;
import io.github.xxyy.common.sql.builder.annotation.SqlValueCache;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

import static io.github.xxyy.common.sql.builder.annotation.SqlValueCache.Type.OBJECT_IDENTIFIER;
import static io.github.xxyy.common.sql.builder.annotation.SqlValueCache.Type.UUID_IDENTIFIER;

/**
 * Base stuff of PlayerWrapper.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 2.4.14
 */
public abstract class PlayerWrapperBase implements SqlValueHolder.DataSource {
    /**
     * Full table name used to store common data. Defaults to {@value io.github.xxyy.common.games.GameLib#XY_DB_NAME}.game_users.
     */
    public static final String FULL_XY_TABLE_NAME = GameLib.XY_DB_NAME + ".game_users";
    public static final String FULL_CENTRAL_USER_TABLE_NAME = GameLib.CENTRAL_DB_NAME + ".user";
    public static final HttpProfileRepository HTTP_PROFILE_REPOSITORY = new HttpProfileRepository();

    /**
     * SafeSql object used by this wrapper to communicate to a database.
     */
    @Getter
    protected final SafeSql sql;
    protected boolean xyFetched = false;
    protected boolean impFetched = false;
    protected boolean xyChanged = false;
    protected boolean isConsole = false;
    protected WeakReference<Player> weakPlr;

    protected GroupData group;

    @SqlValueCache(value = "username", type = OBJECT_IDENTIFIER)
    protected SqlIdentifierHolder<String> plrName;
    @SqlValueCache(value = "uuid", type = UUID_IDENTIFIER)
    protected SqlUUIDHolder uuid;

    @SqlNumberCache(value = "passes_used", numberType = Integer.class)
    protected ConcurrentSqlNumberHolder<Integer> passesUsed;
    @SqlNumberCache(value = "passes_amount", numberType = Integer.class)
    protected ConcurrentSqlNumberHolder<Integer> passesAmount;

    @SqlValueCache("nickname")
    protected SqlValueHolder<String> nick;
    @SqlValueCache("groupname")
    protected SqlValueHolder<String> groupName;

    @SqlNumberCache(value = "coins", numberType = Float.class)
    protected ConcurrentSqlNumberHolder<Float> coins;
    @SqlNumberCache(value = "points", numberType = Integer.class)
    protected ConcurrentSqlNumberHolder<Integer> globalPoints;
    @SqlNumberCache(value = "playtime", numberType = Long.class)
    protected ConcurrentSqlNumberHolder<Long> playtime;
    @SqlNumberCache(value = "kills", numberType = Integer.class)
    protected ConcurrentSqlNumberHolder<Integer> kills;
    @SqlNumberCache(value = "deaths", numberType = Integer.class)
    protected ConcurrentSqlNumberHolder<Integer> deaths;

    protected Collection<SqlValueHolder<?>> valueHolders;
    protected QueryBuilder queryBuilder;

    public PlayerWrapperBase(SafeSql ssql) {
        try {
            this.valueHolders = SqlHolders.processClass(PlayerWrapperBase.class, this, this); //Process base stuff - implementation have to do this themselves to avoid complication with inheritance
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new AssertionError("Could not access some field of "+getClass().getName()+"! This is probably a compile-time failure...Cannot recover.", e);
        }

        this.queryBuilder = new QueryBuilder(FULL_CENTRAL_USER_TABLE_NAME).addAll(valueHolders, true);

        this.sql = ssql;
    }

    /**
     * Initialises tables used by this class.
     *
     * @param ssql SafeSql to use to query the database.
     */
    public static void initTable(SafeSql ssql) {
        ssql.executeUpdate("CREATE DATABASE IF NOT EXISTS " + GameLib.XY_DB_NAME);
        ssql.executeUpdate("CREATE TABLE IF NOT EXISTS " + PlayerWrapper.FULL_XY_TABLE_NAME + " (\n"
                + "    `username` VARCHAR(30) NOT NULL,\n"
                + "    `passes_amount` INT UNSIGNED NOT NULL DEFAULT '0',\n"
                + "    `passes_used` INT UNSIGNED NOT NULL DEFAULT '0',\n"
                + "    `nickname` VARCHAR(30) DEFAULT NULL,\n"
                + "    `groupname` VARCHAR(30) NOT NULL DEFAULT 'default',\n"
                + "    PRIMARY KEY (`username`)\n"
                + ")\n"
                + "COLLATE='utf8_unicode_ci'\n"
                + "ENGINE=MyISAM;");
    }

    /**
     * Forces a full (re-)fetch of all data. This is equivalent to calling {@link io.github.xxyy.common.games.data.PlayerWrapper#impFetch()} and {@link io.github.xxyy.common.games.data.PlayerWrapper#xyFetch()}.
     */
    public void forceFullFetch() {
        this.xyFetch();
        this.impFetch();
    }

    /**
     * Writes all data currently stored in this object to database. This is equivalent to calling {@link io.github.xxyy.common.games.data.PlayerWrapper#impFlush()} and
     * {@link io.github.xxyy.common.games.data.PlayerWrapper#xyFlush()}.
     */
    public void forceFullFlush() {
        this.xyFlush();
        this.impFlush();
    }

    /**
     * Returns the group the wrapped player is in.
     *
     * @return {@link io.github.xxyy.common.games.data.GroupData} the wrapped player is in.
     */
    public GroupData getGroup() {
        if (!this.xyFetched) {
            this.xyFetch();
        }

        return this.group;
    }

    /**
     * Checks if this {@link io.github.xxyy.common.games.data.PlayerWrapper} has a permission. If a {@link org.bukkit.command.ConsoleCommandSender} or {@link org.bukkit.command.BlockCommandSender} was wrapped using
     * {@link io.github.xxyy.common.games.data.PlayerWrapper#PlayerWrapper(org.bukkit.command.CommandSender, io.github.xxyy.common.sql.SafeSql)},
     * {@code true} will always be returned.
     *
     * Regular expressions can be used, for example:
     * {@code game.admin.*} matches
     * {@code game.admin} and, for example,
     * {@code game.admin.awe}
     *
     * @param regex The permission to check for, RegEx are allowed.
     * @return if this {@link io.github.xxyy.common.games.data.PlayerWrapper} is a {@link org.bukkit.command.ConsoleCommandSender} or {@link org.bukkit.command.BlockCommandSender} OR, if it's not, if this
     * {@link io.github.xxyy.common.games.data.PlayerWrapper}'s group has a permission {@code regex}.
     */
    public boolean hasPermission(String regex) {
        return this.isConsole || this.getGroup().hasPermission(regex);
    }

    /**
     * Returns true if the wrapped player is online. Internally, this checks if a {@link org.bukkit.entity.Player} reference is being maintained by this object and, in
     * this case, checks its {@link org.bukkit.entity.Player#isOnline()} method.
     *
     * @return whether the player is online.
     */
    public boolean isOnline() {
        Player plr = this.plr();
        return plr != null && plr.isOnline();
    }

    /**
     * @return The name of the wrapped player. Ignores nicknames.
     * @see org.bukkit.entity.Player#getName()
     */
    public String name() {
        return this.plrName.getValue();
    }

    /**
     * @return The player wrapped by this {@link io.github.xxyy.common.games.data.PlayerWrapper} OR null id the player is offline.
     */
    @Nullable
    public Player plr() {
        if (this.weakPlr == null) {
            Player plr;
            if(this.uuid.getValue() == null) {
                plr = Bukkit.getServer().getPlayerExact(this.name()); //Bukkit#getPlayerExact() is currently deprecated on accident
            } else {
                plr = Bukkit.getPlayer(this.uuid.getValue());
            }

            if (plr == null) {
                return null;//throw new PlayerOfflineException();
            }

            if(this.uuid.getValue() == null){
                this.uuid.updateValue(plr.getUniqueId());
            }

            this.weakPlr = new WeakReference<>(plr);

            return plr;
        }
        Player plr = this.weakPlr.get();
        if (plr == null) {
            return null; //throw new PlayerOfflineException();
        }
        return plr;
    }

    /**
     * Returns the unique ID of the player wrapped by this object, as in {@link org.bukkit.entity.Player#getUniqueId()}. If it is not obtainable (i.e. the player is
     * offline),
     * {@code null} will be returned.
     *
     * @return The Mojang UUID of the wrapped player.
     * @see org.bukkit.entity.Player#getUniqueId()
     * @see io.github.xxyy.common.games.data.PlayerWrapper#plr()
     */
    @Nullable
    public UUID getUniqueId() {
        if (this.uuid.getValue() == null) {
            Player plr = plr();

            if (plr == null) { //TODO: Names are no longer unique
                this.tryFetchByName();
            } else {
                this.uuid.updateValue(plr.getUniqueId());
            }
        }

        return this.uuid.getValue();
    }

    /**
     * Method to be overwritten by implementations. Will be called in {@link io.github.xxyy.common.games.data.PlayerWrapper#forceFullFetch()}. Shall (re-)fetch implementation data.
     *
     * @see io.github.xxyy.common.games.data.PlayerWrapper#xyFetch()
     */
    protected abstract void impFetch();

    /**
     * Method to be overwritten by implementations. Will be called in {@link io.github.xxyy.common.games.data.PlayerWrapper#forceFullFlush()}. Shall save implementation data stored in
     * this object to database.
     *
     * @see io.github.xxyy.common.games.data.PlayerWrapper#xyFlush()
     */
    protected abstract void impFlush();

    /**
     * Re-fetches all data stored by native XYC implementation, i.e. everything included in {@link io.github.xxyy.common.games.data.PlayerWrapper}.
     * It is recommended to call this async.
     */
    final void xyFetch() {
        this.xyFetched = true;

        if (!tryFetchByUUID() && !tryFetchByName() &&
                getUniqueId() != null && name() != null) {
            sql.safelyExecuteUpdate("INSERT INTO " + PlayerWrapperBase.FULL_CENTRAL_USER_TABLE_NAME + " SET username=?, uuid=?", name(), getUniqueId().toString());
            tryFetchByUUID();
        }

        if (getUniqueId() == null) { //This should actually never happen, except for really offline players...not even for them lol TODO: um
            throw new AssertionError("Could not find UUID! This is very bad..." +
                    "I will not attempt to fetch it for you because the name I got is not unique. (Thnx, Mojang, EvilSeph)");
        }
    }

    private boolean tryFetchByIdentifier(SqlIdentifierHolder<?> identifier) { //Returns true if it got the data
        try(QueryResult queryResult = this.queryBuilder.addUniqueIdentifier(identifier)
                .executeSelect(getSql(), false).vouchForResultSet()){
            if(queryResult.rs().next()){
                SqlHolders.updateFromResultSet(this.valueHolders, queryResult.rs());
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            getSql().formatAndPrintException(e, "PlayerWrapperBase#tryFetchByIdentifier");
            this.xyFetched = false;
            return false;
        } finally {
            this.queryBuilder.clearUniqueIdentifiers(); //Make sure we don't have any leftovers
        }
    }

    private boolean tryFetchByUUID() { //Returns true if it got the data
        return this.uuid.isFetched() && getUniqueId() != null && tryFetchByIdentifier(this.uuid);
    }

    private boolean tryFetchByName() { //Returns true if it got the data
        return plrName.getValue() != null && tryFetchByIdentifier(this.plrName);
    }

    /**
     * Writes all data stored in this object to database. It is advisable to this when the wrapped player leaves and also periodically, to be prepared
     * for server crashes.
     */
    final void xyFlush() {
        if (!this.xyChanged) {
            return;
        }

        try {
            this.queryBuilder.addUniqueIdentifier(this.uuid)
                    .executeTrueUpdate(getSql());

            this.xyChanged = false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean select(@NonNull SqlValueHolder<?> holder) {
        forceFullFetch();
        return true;
    }

    @Override
    public void registerChange(@NonNull SqlValueHolder<?> holder) {
        this.xyChanged = true; //TODO only update actually changed values! :)
    }

    /**
     * Checks whether this object has a valid UUID stored, either by it being passed
     * to a constructor or by it being fetched from a database.
     * If this returns {@code false}, {@link #getUniqueId()} will try to fetch the UUID from database, but can
     * return {@code null} if an error occurs.
     * @return whether this object has an UUID stored.
     */
    public boolean hasUniqueId(){
        return this.uuid.isFetched();
    }
}

/*
 * Copyright (c) 2013 - 2015 xxyy (Philipp Nowak; devnull@nowak-at.net). All rights reserved.
 *
 * Any usage, including, but not limited to, compiling, running, redistributing, printing,
 *  copying and reverse-engineering is strictly prohibited without explicit written permission
 *  from the original author and may result in legal steps being taken.
 *
 * See the included LICENSE file (core/src/main/resources) or email xxyy98+xyclicense@gmail.com for details.
 */

package li.l1t.common.games.teams;

import li.l1t.common.games.maps.MapSpawn;
import li.l1t.common.util.CommandHelper;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Represents a team of players.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 05/03/14
 */
public abstract class AbstractTeam implements GameTeam {
    private final String name;
    private Map<Player, Void> playerMap = new WeakHashMap<>(12); //Doubt I'm gonna need more players in a team anytime soon
    private List<MapSpawn> spawns = null;

    @java.beans.ConstructorProperties({"name"})
    public AbstractTeam(String name) {
        this.name = name;
    }

    @Override
    public Collection<Player> getPlayers() {
        return playerMap.keySet();
    }

    @Override
    public void clearPlayers() {
        playerMap.clear();
    }

    @Override
    public Player getPlayer(@Nonnull String plrName) {
        for (Player plr : getPlayers()) {
            if (plr != null && plrName.equals(plr.getName())) {
                return plr;
            }
        }

        return null;
    }

    @Override
    public boolean hasPlayer(Player plr) {
        return playerMap.containsKey(plr);
    }

    @Override
    public boolean hasPlayer(@Nonnull String plrName) {
        return getPlayer(plrName) != null;
    }

    @Override
    public void removePlayer(@Nonnull Player plr) {
        playerMap.remove(plr);
    }

    @Override
    public Player removePlayer(@Nonnull String plrName) {
        for (Iterator<Player> iterator = getPlayers().iterator(); iterator.hasNext(); ) {
            Player plr = iterator.next();
            if (plr != null && plrName.equals(plr.getName())) {
                iterator.remove();
                return plr;
            }
        }

        return null;
    }

    @Override
    public boolean addPlayer(Player plr) {
        if (hasPlayer(plr)) {
            return false;
        }

        playerMap.put(plr, null);
        return true;
    }

    @Override
    public void addSpawn(@Nonnull MapSpawn mapSpawn) {
        if (spawns == null) {
            spawns = new ArrayList<>();
        }

        spawns.add(mapSpawn);
    }

    @Override
    public String toString() {
        return getClass().getName() + ": `" + getName() + "` (" + CommandHelper.safeSize(getSpawns()) + " spawns, " + CommandHelper.safeSize(getPlayers()) + " players)";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof AbstractTeam)) return false;
        final AbstractTeam other = (AbstractTeam) o;
        if (!other.canEqual(this)) return false;
        final Object this$name = this.name;
        final Object other$name = other.name;
        return this$name == null ? other$name == null : this$name.equals(other$name);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.name;
        result = result * PRIME + ($name == null ? 0 : $name.hashCode());
        return result;
    }

    public boolean canEqual(Object other) {
        return other instanceof AbstractTeam;
    }

    public List<MapSpawn> getSpawns() {
        return this.spawns;
    }

    public String getName() {
        return this.name;
    }
}
/*
 * Copyright (c) 2013 - 2015 xxyy (Philipp Nowak; devnull@nowak-at.net). All rights reserved.
 *
 * Any usage, including, but not limited to, compiling, running, redistributing, printing,
 *  copying and reverse-engineering is strictly prohibited without explicit written permission
 *  from the original author and may result in legal steps being taken.
 *
 * See the included LICENSE file (core/src/main/resources) or email xxyy98+xyclicense@gmail.com for details.
 */

package io.github.xxyy.common.misc;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.Nullable;

import io.github.xxyy.common.util.LocationHelper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link org.bukkit.configuration.serialization.ConfigurationSerializable} implementation of {@link org.bukkit.Location}.
 * Note that most instance methods are also available statically in {@link io.github.xxyy.common.util.LocationHelper}.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 22.8.14
 */
public class XyLocation extends Location implements ConfigurationSerializable {
    static {
        ConfigurationSerialization.registerClass(XyLocation.class);
    }

    public XyLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public XyLocation(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    public XyLocation(Location toClone) {
        this(toClone.getWorld(), toClone.getX(), toClone.getY(), toClone.getZ(), toClone.getYaw(), toClone.getPitch());
    }

    /**
     * Creates a command to teleport to this location, using /minecraft:tp.
     *
     * @param playerName the name of the player to teleport ot NULL to teleport the executing player.
     * @return a command that teleports to this location
     * @see io.github.xxyy.common.util.LocationHelper#createTpCommand(org.bukkit.Location, String)
     */
    public String toTpCommand(@Nullable String playerName) {
        return LocationHelper.createTpCommand(this, playerName);
    }

    /**
     * Checks if this location's block is in between or at the border of two boundaries.
     *
     * @param boundary1 the first boundary
     * @param boundary2 the second boundary
     * @return whether this location is in or at the border of the cuboid region represented by the boundaries
     * @see io.github.xxyy.common.util.LocationHelper#isBlockBetween(org.bukkit.Location, org.bukkit.Location, org.bukkit.Location)
     */
    public boolean isInBetween(@Nonnull Location boundary1, @Nonnull Location boundary2) {
        return LocationHelper.isBlockBetween(this, boundary1, boundary2);
    }

    /**
     * @param radius the maximal distance from this location
     * @return a random location which is no more than {@code radius} away from this location.
     * @see io.github.xxyy.common.util.LocationHelper#randomiseLocation(org.bukkit.Location, int)
     */
    public Location randomize(int radius) {
        return LocationHelper.randomiseLocation(this, radius);
    }

    /**
     * @return a human-readable string containing world and block coordinates of this location
     * @deprecated Misspelling of {@link #prettyPrint()}
     */
    @Deprecated
    public String pretyPrint() {
        return prettyPrint();
    }

    /**
     * @return a human-readable string containing world and block coordinates of this location
     * @see io.github.xxyy.common.util.LocationHelper#prettyPrint(org.bukkit.Location)
     */
    public String prettyPrint() {
        return LocationHelper.prettyPrint(this);
    }

    /**
     * Checks if the given location is in the same world and at the same block coordinates as this location.
     *
     * @param other the location to compare against
     * @return whether the given location is "about the same" as this one
     * @see io.github.xxyy.common.util.LocationHelper#softEqual(org.bukkit.Location, org.bukkit.Location)
     */
    public boolean softEquals(@Nonnull Location other) {
        return LocationHelper.softEqual(this, other);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();

        result.put("world", getWorld().getName());
        result.put("x", getX());
        result.put("y", getY());
        result.put("z", getZ());
        result.put("pitch", getPitch());
        result.put("yaw", getYaw());

        return result;
    }

    /**
     * Serializes this location to a string.
     *
     * @return a string representing this location
     * @see io.github.xxyy.common.util.LocationHelper#serialize(org.bukkit.Location)
     */
    public String serializeToString() {
        return LocationHelper.serialize(this);
    }

    public static XyLocation deserialize(Map<String, Object> data) {
        Preconditions.checkNotNull(data, "Input data cannot be null!");
        Preconditions.checkArgument(data.containsKey("world"), "Must have world key!");
        Preconditions.checkArgument(data.containsKey("x"), "Must have x key!");
        Preconditions.checkArgument(data.containsKey("y"), "Must have y key!");
        Preconditions.checkArgument(data.containsKey("z"), "Must have z key!");

        World world = Bukkit.getWorld(data.get("world").toString());
        Preconditions.checkNotNull(world, "Unknown world!");

        float pitch = 0f, yaw = 0f;
        if (data.containsKey("pitch")) {
            pitch = Float.parseFloat(data.get("pitch").toString());
        }
        if (data.containsKey("yaw")) {
            yaw = Float.parseFloat(data.get("yaw").toString());
        }

        return new XyLocation(world,
                Double.parseDouble(data.get("x").toString()),
                Double.parseDouble(data.get("y").toString()),
                Double.parseDouble(data.get("z").toString()),
                yaw,
                pitch);
    }

    /**
     * Attempts to find a location encoded in a SQL {@link ResultSet}. This looks for column
     * names "x", "y", "z" and "world". Note that this doesn't account for pitch and yaw.
     *
     * @param rs the result set to use
     * @return a location derived from the argument
     * @throws SQLException         if any SQL error occurs or any of the columns doesn't exist
     * @throws NullPointerException if given world doesn't exist
     */
    public static XyLocation fromResultSet(ResultSet rs) throws SQLException {
        Preconditions.checkNotNull(rs, "resultSet");

        World world = Bukkit.getWorld(rs.getString("world"));
        Preconditions.checkNotNull(world, "Unknown world!");

        return new XyLocation(world, rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
    }

    /**
     * Attempts to find a location encoded in a SQL {@link ResultSet}. This looks for column
     * names "x", "y", "z", "pitch", "yaw" and "world".
     *
     * @param rs the result set to use
     * @return a location derived from the argument
     * @throws SQLException         if any SQL error occurs or any of the columns doesn't exist
     * @throws NullPointerException if given world doesn't exist
     */
    public static XyLocation fromResultSetPitchYaw(ResultSet rs) throws SQLException {
        Preconditions.checkNotNull(rs, "resultSet");

        World world = Bukkit.getWorld(rs.getString("world"));
        Preconditions.checkNotNull(world, "Unknown world!");

        return new XyLocation(world, rs.getInt("x"), rs.getInt("y"), rs.getInt("z"),
                rs.getFloat("yaw"), rs.getFloat("pitch"));
    }


    /**
     * Wraps a location as a {@link XyLocation} or returns the argument if it is already wrapped.
     *
     * @param loc the source location
     * @return a {@link XyLocation} equivalent to the argument
     */
    public static XyLocation of(Location loc) {
        Preconditions.checkNotNull(loc, "loc may not be null @XyLocation#of");
        return loc instanceof XyLocation ? (XyLocation) loc : new XyLocation(loc);
    }
}

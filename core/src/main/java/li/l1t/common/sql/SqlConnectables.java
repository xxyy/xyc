/*
 * Copyright (c) 2013 - 2015 xxyy (Philipp Nowak; devnull@nowak-at.net). All rights reserved.
 *
 * Any usage, including, but not limited to, compiling, running, redistributing, printing,
 *  copying and reverse-engineering is strictly prohibited without explicit written permission
 *  from the original author and may result in legal steps being taken.
 *
 * See the included LICENSE file (core/src/main/resources) or email xxyy98+xyclicense@gmail.com for details.
 */

package li.l1t.common.sql;

import org.apache.commons.lang.Validate;

import javax.annotation.Nonnull;

/**
 * Statuic utilities for the {@link SqlConnectable} class.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 14.5.14
 */
public final class SqlConnectables {

    private SqlConnectables() {
    }

    /**
     * Returns the host string for the given Connectable. The host string consists of
     * the database URL, as returned by {@link SqlConnectable#getSqlHost()}, of a slash (/)
     * and the database name, as returned by {@link SqlConnectable#getSqlDb()}.
     * If the database name is already appended to the host, it will not be appended another time.
     *
     * @param connectable Connectable to get information from
     * @return Host string, as accepted by database drivers.
     */
    public static String getHostString(@Nonnull SqlConnectable connectable) {
        String sqlHost = connectable.getSqlHost();

        Validate.notNull(sqlHost);

        return getHostString(connectable.getSqlDb(), sqlHost);
    }

    /**
     * Returns the host string for the given parameters. The host string consists of
     * the database URL, as returned by {@link SqlConnectable#getSqlHost()}, of a slash (/)
     * and the database name, as returned by {@link SqlConnectable#getSqlDb()}.
     * If the database name is already appended to the host, it will not be appended another time.
     *
     * @param database Database to use
     * @param sqlHost  Fully-qualified JDBC connection string of the host to use
     * @return Host string, as accepted by database drivers.
     */
    public static String getHostString(String database, String sqlHost) {
        String rtrn = sqlHost.contains(database) ? sqlHost : (sqlHost.endsWith("/") ? (sqlHost + database) : (sqlHost + "/" + database));

        if (!rtrn.startsWith("jdbc:")) {
            if (!rtrn.startsWith("mysql://")) {
                rtrn = "jdbc:mysql://" + rtrn;
            } else {
                rtrn = "jdbc:" + rtrn;
            }
        }

        if (!rtrn.contains("?")) {
            rtrn += "?autoReconnect=true";
        }

        return rtrn;
    }

    /**
     * Creates a simple SqlConnectable that simply returns the connection credentials provided.
     *
     * @param host     {@link SqlConnectable#getSqlHost()}
     * @param database {@link SqlConnectable#getSqlDb()}
     * @param user     {@link SqlConnectable#getSqlUser()}
     * @param password {@link SqlConnectable#getSqlPwd()}
     * @return A simple SqlConnectable matching provided arguments.
     */
    @Nonnull
    public static SqlConnectable fromCredentials(@Nonnull final String host, @Nonnull final String database,
                                                 @Nonnull final String user, @Nonnull final String password) {
        return new SqlConnectable() {
            @Override
            public String getSqlDb() {
                return database;
            }

            @Override
            public String getSqlHost() {
                return host;
            }

            @Override
            public String getSqlPwd() {
                return password;
            }

            @Override
            public String getSqlUser() {
                return user;
            }
        };
    }

    /**
     * Returns the host string for the given parameters. The host string consists of
     * the database URL, as returned by {@link SqlConnectable#getSqlHost()}, of a slash (/)
     * and the database name, as returned by {@link SqlConnectable#getSqlDb()}.
     * If the database name is already appended to the host, it will not be appended another time.
     *
     * @param database Database to use
     * @param hostname Fully-qualified JDBC connection string of the host to use or just a FQDN.
     * @param port     port number to use.
     * @return Host string, as accepted by database drivers.
     */
    public static String getHostString(String hostname, int port, String database) {
        String sqlHost;

        if (!hostname.startsWith("jdbc:")) {
            sqlHost = "jdbc:mysql://" + hostname + ":" + port + "/";
        } else {
            sqlHost = hostname;
        }

        return getHostString(database, sqlHost);
    }
}
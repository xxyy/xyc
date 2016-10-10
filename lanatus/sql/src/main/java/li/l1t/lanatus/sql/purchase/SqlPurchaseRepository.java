/*
 * Copyright (c) 2013 - 2015 xxyy (Philipp Nowak; devnull@nowak-at.net). All rights reserved.
 *
 * Any usage, including, but not limited to, compiling, running, redistributing, printing,
 *  copying and reverse-engineering is strictly prohibited without explicit written permission
 *  from the original author and may result in legal steps being taken.
 *
 * See the included LICENSE file (core/src/main/resources) or email xxyy98+xyclicense@gmail.com for details.
 */

package li.l1t.lanatus.sql.purchase;

import li.l1t.common.collections.IdCache;
import li.l1t.lanatus.api.exception.NoSuchRowException;
import li.l1t.lanatus.api.purchase.Purchase;
import li.l1t.lanatus.api.purchase.PurchaseRepository;
import li.l1t.lanatus.sql.AbstractSqlLanatusRepository;
import li.l1t.lanatus.sql.SqlLanatusClient;

import java.util.Collection;
import java.util.UUID;

/**
 * Simple repository for purchases backed by a JDBC SQL data source. Caches id lookups.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-10
 */
public class SqlPurchaseRepository extends AbstractSqlLanatusRepository implements PurchaseRepository {
    public static final String TABLE_NAME = "mt_main.lanatus_purchases";
    private final IdCache<Purchase> cache = new IdCache<>();
    private final JdbcPurchaseFetcher fetcher = new JdbcPurchaseFetcher(
            new JdbcPurchaseCreator(client().products()), client().sql()
    );

    public SqlPurchaseRepository(SqlLanatusClient client) {
        super(client);
    }

    @Override
    public Purchase findById(UUID purchaseId) throws NoSuchRowException {
        return cache.getOrCompute(purchaseId, fetcher::fetchById);
    }

    @Override
    public Collection<Purchase> findByPlayer(UUID playerId) {
        return fetcher.fetchByPlayer(playerId);
    }

    @Override
    public void clearCache() {
        cache.clear();
    }
}

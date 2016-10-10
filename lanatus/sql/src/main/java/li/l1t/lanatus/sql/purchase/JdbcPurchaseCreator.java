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

import li.l1t.lanatus.api.exception.NoSuchRowException;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.api.product.ProductRepository;
import li.l1t.lanatus.api.purchase.Purchase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

/**
 * Creates purchase objects from JDBC SQL result sets.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-10
 */
class JdbcPurchaseCreator {
    private final ProductRepository productRepository;

    JdbcPurchaseCreator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Purchase createFromCurrentRow(ResultSet rs) throws SQLException, NoSuchRowException {
        return new SqlPurchase(
                uuid(rs, "id"), uuid(rs, "player_uuid"),
                findProduct(rs), rs.getObject("created", Instant.class),
                rs.getString("data"), rs.getString("comment"), rs.getInt("melonscost")
        );
    }

    private Product findProduct(ResultSet rs) throws SQLException, NoSuchRowException {
        return productRepository.findById(uuid(rs, "product_id"));
    }

    private UUID uuid(ResultSet rs, String column) throws SQLException {
        return UUID.fromString(rs.getString(column));
    }
}

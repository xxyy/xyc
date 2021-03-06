/*
 * MIT License
 *
 * Copyright (C) 2013 - 2017 Philipp Nowak (https://github.com/xxyy) and contributors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package li.l1t.lanatus.sql.purchase;

import li.l1t.lanatus.api.account.MutableAccount;
import li.l1t.lanatus.api.builder.PurchaseBuilder;
import li.l1t.lanatus.api.exception.AccountConflictException;
import li.l1t.lanatus.api.exception.NoSuchPurchaseException;
import li.l1t.lanatus.api.product.Product;
import li.l1t.lanatus.api.purchase.Purchase;
import li.l1t.lanatus.sql.AbstractLanatusSqlTest;
import li.l1t.lanatus.sql.SqlLanatusClient;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.UUID;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

/**
 * Tests the integration of the sql purchase repository with the database, verifying only using the
 * repository's own methods.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-20
 */
public class SqlPurchaseRepositoryTest extends AbstractLanatusSqlTest {
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static UUID PURCHASE_ID = null;

    @BeforeClass
    public static void setupSampleData() throws AccountConflictException {
        SqlLanatusClient client = createClient();
        MutableAccount mutable = client.accounts().findMutable(PLAYER_ID);
        mutable.setMelonsCount(1337);
        client.accounts().save(mutable);
        Product product = client.products().registration(PRODUCT_ID).register();
        PurchaseBuilder builder = client.startPurchase(PLAYER_ID).withProduct(product);
        builder.build();
        PURCHASE_ID = builder.getPurchaseId();
    }

    @Test
    public void testFindById__basic() {
        //given PURCHASE_ID
        //when
        Purchase purchase = repo().findById(PURCHASE_ID);
        //then
        assertNotNull(purchase);
        assertThat(purchase.getPlayerId(), is(PLAYER_ID));
        assertThat(purchase.getUniqueId(), is(PURCHASE_ID));
    }

    @Test(expected = NoSuchPurchaseException.class)
    public void testFindById__nonExisting() {
        //given
        UUID nonExistingId = UUID.randomUUID();
        //when
        repo().findById(nonExistingId);
        //then an exception is thrown
    }

    @Test
    public void testFindById__caching() {
        //given
        Purchase initial = repo().findById(PURCHASE_ID);
        //when
        Purchase cached = repo().findById(PURCHASE_ID);
        //then
        assertSame("purchases are not cached", initial, cached);
    }

    @Test
    public void testFindByPlayer__basic() {
        //given PURCHASE_ID, PLAYER_ID
        //when
        Collection<Purchase> results = repo().findByPlayer(PLAYER_ID);
        //then
        assertThat(results, hasSize(1));
        assertThat(results, contains(hasProperty("uniqueId", is(PURCHASE_ID))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindByPlayer__multiple() {
        //given
        PurchaseBuilder builder = client().startPurchase(PLAYER_ID).withProductId(PRODUCT_ID);
        builder.build();
        UUID secondId = builder.getPurchaseId();
        //when
        Collection<Purchase> results = repo().findByPlayer(PLAYER_ID);
        //then
        assertThat(results, hasSize(2));
        assertThat(results, containsInAnyOrder( // <-- unchecked
                hasProperty("uniqueId", is(PURCHASE_ID)),
                hasProperty("uniqueId", is(secondId))
        ));
    }

    @Test
    public void testClearCache() {
        //given
        Purchase initial = repo().findById(PURCHASE_ID);
        //when
        repo().clearCache();
        //then
        assertNotSame("cache not cleared", initial, repo().findById(PURCHASE_ID));
    }

    private SqlPurchaseRepository repo() {
        return client().purchases();
    }
}

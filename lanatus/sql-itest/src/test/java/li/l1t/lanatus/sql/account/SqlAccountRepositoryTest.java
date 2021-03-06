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

package li.l1t.lanatus.sql.account;

import li.l1t.lanatus.api.account.AccountSnapshot;
import li.l1t.lanatus.api.account.MutableAccount;
import li.l1t.lanatus.api.exception.AccountConflictException;
import li.l1t.lanatus.sql.AbstractLanatusSqlTest;
import li.l1t.lanatus.sql.SqlLanatusClient;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

/**
 * Tests the integration of the sql account repository with the database, verifying only using the
 * repository's own methods.
 *
 * @author <a href="https://l1t.li/">Literallie</a>
 * @since 2016-10-20
 */
public class SqlAccountRepositoryTest extends AbstractLanatusSqlTest {
    private static final UUID PLAYER_ID = UUID.randomUUID();
    private static final String EXPECTED_RANK = "testrank";

    @BeforeClass
    public static void setupSampleData() throws AccountConflictException {
        SqlLanatusClient client = createClient();
        MutableAccount account = client.accounts().findMutable(PLAYER_ID);
        account.setLastRank(EXPECTED_RANK);
        account.setMelonsCount(420);
        client.accounts().save(account);
    }

    @Test
    public void testFind__basic() {
        //given PLAYER_ID
        //when
        AccountSnapshot firstSnapshot = findAccount(PLAYER_ID);
        //then
        assertNotNull("firstSnapshot", firstSnapshot);
        assertThat("rank does not match expected", firstSnapshot.getLastRank(), is(EXPECTED_RANK));
    }

    @Test
    public void testFind__caching() {
        //given PLAYER_ID
        //when
        AccountSnapshot first = findAccount(PLAYER_ID);
        AccountSnapshot second = findAccount(PLAYER_ID);
        //then
        assertSame("cache does not return the same object every time", first, second);
    }

    @Test
    public void testRefresh__notSame() {
        //given
        AccountSnapshot initial = findAccount(PLAYER_ID);
        //when
        AccountSnapshot refreshed = repo().refresh(initial);
        //then
        assertNotSame("refresh does not return new object", initial, refreshed);
        assertNotSame("refresh does not purge cache", initial, findAccount(PLAYER_ID));
        assertSame("refresh does not cache new object", refreshed, findAccount(PLAYER_ID));
    }

    @Test
    public void testClearCache__single() {
        //given
        AccountSnapshot initial = findAccount(PLAYER_ID);
        //when
        repo().clearCache();
        //then
        assertNotSame("clear cache does not purge cache", initial, findAccount(PLAYER_ID));
    }

    @Test
    public void testFindMutable__sameData() {
        //given
        AccountSnapshot snapshot = findAccount(PLAYER_ID);
        //when
        MutableAccount mutable = repo().findMutable(PLAYER_ID);
        //then
        assertNotNull(mutable);
        assertThat(mutable.getInitialState(), is(equalTo(snapshot)));
    }

    @Test
    public void testSave__noChanges() throws AccountConflictException {
        //given
        MutableAccount mutable = repo().findMutable(PLAYER_ID);
        //when
        repo().save(mutable);
        //then
        assertNotNull(mutable);
        repo().clearCache();
        assertThat(mutable.getInitialState(), is(equalTo(findAccount(PLAYER_ID))));
    }

    @Test
    public void testSave__changes() throws AccountConflictException {
        //given
        MutableAccount mutable = repo().findMutable(PLAYER_ID);
        mutable.modifyMelonsCount(-20);
        //when
        repo().save(mutable);
        //then
        assertNotNull(mutable);
        repo().clearCache();
        assertThat(findAccount(PLAYER_ID), is(equalTo(mutable)));
    }

    @Test
    public void testSave__concurrent_changes() throws AccountConflictException {
        //given
        MutableAccount mutable = repo().findMutable(PLAYER_ID);
        mutable.modifyMelonsCount(-20);
        givenThatTheAccountWasUpdatedConcurrently(+50);
        //when
        repo().save(mutable);
        //then
        assertNotNull(mutable);
        thenTheRemoteMelonsCountHasChangedBy(mutable, 50 - 20);
    }

    private void thenTheRemoteMelonsCountHasChangedBy(MutableAccount mutable, int expectedModifier) {
        repo().clearCache();
        int remoteMelonsCount = findAccount(PLAYER_ID).getMelonsCount();
        int initialMelonsCount = mutable.getInitialState().getMelonsCount();
        assertThat(remoteMelonsCount, is(initialMelonsCount + expectedModifier));
    }

    private void givenThatTheAccountWasUpdatedConcurrently(int modifier) throws AccountConflictException {
        MutableAccount concurrent = repo().findMutable(PLAYER_ID);
        concurrent.modifyMelonsCount(modifier);
        repo().save(concurrent);
    }

    private SqlAccountRepository repo() {
        return client().accounts();
    }
}

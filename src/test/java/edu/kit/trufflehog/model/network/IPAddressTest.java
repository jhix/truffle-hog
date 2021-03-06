package edu.kit.trufflehog.model.network;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 *     This class contains all tests for the IPAddress class.
 * </p>
 * @author Mark Giraud
 */
public class IPAddressTest {

    @Test(expected = InvalidIPAddress.class)
    public void IPAddress_constructor_throws_on_negative_address() throws Exception {
        new IPAddress(-1);
    }

    @Test(expected = InvalidIPAddress.class)
    public void IPAddress_constructor_throws_on_too_large_address() throws Exception {
        new IPAddress(0x100000000L);
    }

    @Test
    public void toByteArray_returns_correct_values() throws Exception {
        final IPAddress ipAddress = new IPAddress(2071538313L);

        byte[] bytes = ipAddress.toByteArray();
        assertEquals(0b01111011, bytes[0] & 0xFF);
        assertEquals(0b01111001, bytes[1] & 0xFF);
        assertEquals(0b00101010, bytes[2] & 0xFF);
        assertEquals(0b10001001, bytes[3] & 0xFF);
    }

    @Test
    public void address_is_multicast() throws Exception {
        for (long i = 3758096384L; i <= 4026531839L; i += 10000) {
            assertTrue("IP " + new IPAddress(i) + " should be multicast", new IPAddress(i).isMulticast());
        }
    }

    @Test
    public void address_is_not_multicast() throws Exception {
        for (long i = 1; i < 3758096384L; i += 100000) {
            assertFalse(new IPAddress(i).isMulticast());
        }
        for (long i = 4026531840L; i <= 4294967295L; i += 100000) {
            assertFalse(new IPAddress(i).isMulticast());
        }
    }

    @Test
    public void same_addresses_equal_each_other() throws Exception {
        long i = 1;
        while (i <= 4294967295L) {
            assertTrue(new IPAddress(i).equals(new IPAddress(i)));
            i += Math.random() * 100000;
        }
    }

    @Test
    public void different_addresses_do_not_equal_each_other() throws Exception {
        long i = 1;
        while (i <= 4294967295L) {
            assertFalse(new IPAddress(i).equals(new IPAddress((i + (long)(Math.random() * 1000)) % 4294967295L + 1)));
            i += Math.random() * 100000;
        }
    }

    @Test
    public void size_is_32() throws Exception {
        assertEquals(32, new IPAddress(123).size());
    }

    @Test
    public void hashCode_of_same_addresses_is_the_same() throws Exception {
        IPAddress a = new IPAddress(123);
        IPAddress a1 = new IPAddress(123);

        IPAddress b = new IPAddress(234);
        IPAddress b1 = new IPAddress(234);

        assertEquals(a.hashCode(), a1.hashCode());
        assertEquals(b.hashCode(), b1.hashCode());
    }

    @Test
    public void hashCode_of_different_addresses_is_different() throws Exception {
        IPAddress a = new IPAddress(123);
        IPAddress b = new IPAddress(234);

        IPAddress a1 = new IPAddress(654);
        IPAddress b1 = new IPAddress(987);

        assertNotEquals(a, b);
        assertNotEquals(a1, b1);
    }

    @Test
    public void compareTo_small_ip_is_less_than_big_ip() throws Exception {
        IPAddress a = new IPAddress(1);
        IPAddress b = new IPAddress(2);

        assertTrue("IPAddress(1) should be less than IPAddress(2) but is not", a.compareTo(b) < 0);

        IPAddress a1 = new IPAddress(1);
        IPAddress b1 = new IPAddress(0xFFFFFFL);

        assertTrue("IPAddress(1) should be less than IPAddress(4294967296) but is not", a1.compareTo(b1) < 0);
    }

    @Test
    public void compareTo_big_ip_is_greater_than_small_ip() throws Exception {
        IPAddress a = new IPAddress(2);
        IPAddress b = new IPAddress(1);

        assertTrue("IPAddress(2) should be greater than IPAddress(1) but is not", a.compareTo(b) > 0);

        IPAddress a1 = new IPAddress(0xFFFFFFL);
        IPAddress b1 = new IPAddress(1);

        assertTrue("IPAddress(4294967296) should be greater than IPAddress(1) but is not", a1.compareTo(b1) > 0);
    }
}